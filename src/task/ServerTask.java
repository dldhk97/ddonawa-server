package task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import model.Account;
import model.BigCategory;
import model.Category;
import model.CollectedInfo;
import model.Favorite;
import model.Product;
import model.Tuple;
import network.Direction;
import network.EventType;
import network.Protocol;
import network.ProtocolType;
import network.Response;
import network.ResponseType;
import utility.IOHandler;

public class ServerTask implements Runnable{
	
	private final Socket clientSocket;
	private final String clientIP;
	
	public ServerTask(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.clientIP = clientSocket.getInetAddress().toString();
	}

	@Override
	public void run() {
		IOHandler.getInstance().log("[" + clientIP + "] 스레드 생성 완료");
		
		try {
			ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			// 일단 프로토콜로 해석
			Protocol receivedProtocol = (Protocol) inputStream.readObject();
			
			// 프로토콜의 타입이 무엇인가?
			switch(receivedProtocol.getType()){
				case LOGIN:
					onLogin(receivedProtocol);
					break;
				case REGISTER:
					onRegister(receivedProtocol);
					break;
				case EVENT:
					onEvent(receivedProtocol);
					break;
				default :
					onUnknown(receivedProtocol);
					break;
			}
			
			clientSocket.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// 소켓 다시한번 종료
		if(clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		IOHandler.getInstance().log("[" + clientIP + "] 스레드 종료됨");
	}
	
	private void sendOutputStream(Protocol protocol) throws Exception {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectOutputStream.writeObject(protocol);
		objectOutputStream.flush();
	}
	
	private void onLogin(Protocol receivedProtocol) throws Exception{
		// 로그인이면 계정 작업 생성
		AccountTask at = new AccountTask();
		
		// 사용자에게서 받아온 계정 정보 획득 후 로그인 시도
		Account account = (Account) receivedProtocol.getObject();
		Response response = at.tryLogin(account);
		Protocol sendProtocol = new Protocol(ProtocolType.LOGIN, Direction.TO_CLIENT, response, null);
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onRegister(Protocol receivedProtocol) throws Exception{
		// 회원가입 계정 작업 생성
		AccountTask at = new AccountTask();
		
		// 사용자에게서 받아온 계정 정보 획득 후 회원가입 시도
		Account account = (Account) receivedProtocol.getObject();
		Response response = at.register(account);
		Protocol sendProtocol = new Protocol(ProtocolType.REGISTER, Direction.TO_CLIENT, response, null);
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	// 이벤트는 경우의 수가 많기 때문에 한번 더 getEventType으로 케이스 분기함.
	// 이벤트 관련 Task들은 리턴값으로 Tuple을 받아, Response와 Object를 같이 받을 수 있게 함.
	private void onEvent(Protocol receivedProtocol) throws Exception{
		switch(receivedProtocol.getEventType()) {
			case GET_BIG_CATEGORY:
				onGetBigCategory(receivedProtocol);
				IOHandler.getInstance().log("[" + clientIP + "] 대분류 결과 반환 완료");
				break;
			case GET_CATEGORY:
				onGetCategory(receivedProtocol);
				IOHandler.getInstance().log("[" + clientIP + "] 분류 결과 반환 완료");
				break;
			case SEARCH:
				onSearch(receivedProtocol);
				IOHandler.getInstance().log("[" + clientIP + "] 검색 결과 반환 완료");
				break;
			case GET_PRODUCT_DETAIL:
				onGetProductDetail(receivedProtocol);
				IOHandler.getInstance().log("[" + clientIP + "] 상세 정보 반환 완료");
				break;
			case SEARCH_BY_CATEGORY:
				onSearchByCategory(receivedProtocol);
				break;
			case ADD_FAVORITE:
				onAddFavorite(receivedProtocol);
				break;
			case DELETE_FAVORITE:
				onDeleteFavorite(receivedProtocol);
				break;
			case GET_FAVORITE:
				onGetFavorite(receivedProtocol);
				break;
			case REQUEST_FAVORITE_CHECK:
				onRequestFavoriteCheck(receivedProtocol);
				break;
			default:
				onUnknown(receivedProtocol);
				IOHandler.getInstance().log("[" + clientIP + "] 이벤트 타입을 모르겠어요");
				break;
		}
	}
	
	private void onSearch(Protocol receivedProtocol) throws Exception {
		// 상품 작업 생성
		ProductTask pt = new ProductTask();
		
		IOHandler.getInstance().log("[DEBUG-" + Thread.currentThread().getId() + "] 검색 요청");
		// 사용자에게서 받아온 검색어 획득 후 검색
		String searchWord = (String) receivedProtocol.getObject();
		Tuple<Response, ArrayList<Product>> productResult = pt.searchByProductName(searchWord);
		
		// 상품정보 응답 및 검색결과 받아옴
		Response response = productResult.getFirst();
		ArrayList<Product> productList = productResult.getSecond();
		
		IOHandler.getInstance().log("[DEBUG-" + Thread.currentThread().getId() + "] 최근 수집정보 목록 획득 요청");
		// 상품정보 - 최근가격정보가 쌍으로 이루어진 결과 배열 생성
		ArrayList<Tuple<Product, CollectedInfo>> totalResult = getRecentProductTupleList(productList);
		
		// 상품정보+최신수집정보를 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.SEARCH, response, (Object)totalResult);
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	// 상품정보 - 최근가격정보가 쌍으로 이루어진 배열 생성
	private ArrayList<Tuple<Product, CollectedInfo>> getRecentProductTupleList(ArrayList<Product> productList){
		ArrayList<Tuple<Product, CollectedInfo>> totalResult = new ArrayList<Tuple<Product,CollectedInfo>>();
		
		// 상품정보가 존재한다면 최근 가격도 가져온다.
		if(productList != null && productList.size() > 0) {
			CollectedInfoTask cit = new CollectedInfoTask();
			// 상품정보 하나씩, 최근 가격 가져옴
			for(Product p : productList) {
				Tuple<Response, ArrayList<CollectedInfo>> collectedInfoResult = cit.findByProduct(p);
				ArrayList<CollectedInfo> collectedInfoList = collectedInfoResult.getSecond();
				
				// 최근 가격 가져와서 최종 결과 배열에 추가.
				if(collectedInfoList != null && collectedInfoList.size() > 0) {
					CollectedInfo ci = collectedInfoList.get(0);
					totalResult.add(new Tuple<Product, CollectedInfo>(p, ci));
				}
				
			}
		}
		
		return totalResult.size() > 0 ? totalResult : null;
	}
	
	private void onSearchByCategory(Protocol receivedProtocol) throws Exception {
		// 상품 작업 생성
		ProductTask pt = new ProductTask();
		
		// 사용자에게서 받아온 카테고리 획득 후 검색
		Category category = (Category) receivedProtocol.getObject();
		Tuple<Response, ArrayList<Product>> productResult = pt.searchByCategory(category);
		
		// 상품정보 응답 및 검색결과 받아옴
		Response response = productResult.getFirst();
		ArrayList<Product> productList = productResult.getSecond();
		
		// 상품정보 - 최근가격정보가 쌍으로 이루어진 결과 배열 생성
		ArrayList<Tuple<Product, CollectedInfo>> totalResult = new ArrayList<Tuple<Product,CollectedInfo>>();
		
		// 상품정보가 존재한다면 최근 가격도 가져온다.
		if(productList != null && productList.size() > 0) {
			CollectedInfoTask cit = new CollectedInfoTask();
			// 상품정보 하나씩, 최근 가격 가져옴
			for(Product p : productList) {
				Tuple<Response, ArrayList<CollectedInfo>> collectedInfoResult = cit.findByProduct(p);
				ArrayList<CollectedInfo> collectedInfoList = collectedInfoResult.getSecond();
				
				// 최근 가격 가져와서 최종 결과 배열에 추가.
				if(collectedInfoList != null && collectedInfoList.size() > 0) {
					CollectedInfo ci = collectedInfoList.get(0);
					totalResult.add(new Tuple<Product, CollectedInfo>(p, ci));
				}
				
			}
		}
		
		// 상품정보+최신수집정보를 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.SEARCH_BY_CATEGORY, response, (Object)totalResult);
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onGetProductDetail(Protocol receivedProtocol) throws Exception{
		// 수집 정보 작업 생성
		CollectedInfoTask cit = new CollectedInfoTask();
		
		// 사용자에게서 받아온 상품정보 획득
		Product product = (Product) receivedProtocol.getObject();
		
		// 상품정보 파싱 해서 업데이트한다!
		Response collectResponse = cit.collect(product);
		switch (collectResponse.getResponseType()) {
		case SUCCEED:
			IOHandler.getInstance().log("[" + clientIP + "] 수집 정보 업데이트 성공");
			break;
		default:
			IOHandler.getInstance().log("["+ clientIP + "] 수집 정보 업데이트 실패. 그냥 기존 수집정보만 가져옴");
			break;
		}
		
		// (업데이트된) 수집정보 가져온다.
		Tuple<Response, ArrayList<CollectedInfo>> result = cit.findByProduct(product);
		
		// 응답 및 검색결과 받아옴
		Response response = result.getFirst();
		ArrayList<CollectedInfo> collectedInfoList = result.getSecond();
		
		// 수집 정보를 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.GET_PRODUCT_DETAIL, response, (Object)collectedInfoList); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onGetBigCategory(Protocol receivedProtocol) throws Exception{
		// 대분류 작업 생성
		BigCategoryTask bct = new BigCategoryTask();
		
		// 대분류 다 가져옴
		Tuple<Response, ArrayList<BigCategory>> result = bct.getAllBigCategory();
		
		// 응답과, 대분류 분리
		Response response = result.getFirst();
		ArrayList<BigCategory> bigCategoryList = result.getSecond();
		
		// 대분류 목록을 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.GET_BIG_CATEGORY, response, (Object)bigCategoryList); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onGetCategory(Protocol receivedProtocol) throws Exception{
		// 분류 작업 생성
		CategoryTask ct = new CategoryTask();
		
		// 사용자에게서 대분류 받음. 그걸로 검색
		BigCategory bigCategory = (BigCategory) receivedProtocol.getObject();
		Tuple<Response, ArrayList<Category>> result = ct.findByBigCategory(bigCategory);
		
		// 응답과, 분류 분리
		Response response = result.getFirst();
		ArrayList<Category> categoryList = result.getSecond();
		
		// 분류 목록 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.GET_CATEGORY, response, (Object)categoryList); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onAddFavorite(Protocol receivedProtocol) throws Exception {
		// 찜 작업 생성
		FavoriteTask ft = new FavoriteTask();
		
		// 사용자에게서 찜 받음. 그걸로 추가시도
		Favorite favorite = (Favorite) receivedProtocol.getObject();
		Response response = ft.addFavorite(favorite, false);
		
		// 분류 목록 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.ADD_FAVORITE, response, null); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onDeleteFavorite(Protocol receivedProtocol) throws Exception {
		// 찜 작업 생성
		FavoriteTask ft = new FavoriteTask();
		
		// 사용자에게서 찜 받음. 그걸로 삭제시도
		Favorite favorite = (Favorite) receivedProtocol.getObject();
		Response response = ft.deleteFavorite(favorite);
		
		// 분류 목록 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.DELETE_FAVORITE, response, null); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	private void onGetFavorite(Protocol receivedProtocol) throws Exception {
		// 찜 작업 생성
		FavoriteTask ft = new FavoriteTask();
		
		// 사용자에게서 계정정보 받음
		Account account = (Account) receivedProtocol.getObject();
		Tuple<Response, ArrayList<Favorite>> result = ft.findByAccount(account);
		
		// 응답과, 분류 분리
		Response response = result.getFirst();
		ArrayList<Favorite> favoriteList = result.getSecond();
		
		// 분류 목록 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.GET_FAVORITE, response, favoriteList); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	// 주기적 파싱해서 체크해서 돌려줌. 사용자에게서 Account를 받아 ArrayList<Product>를 돌려줌
	private void onRequestFavoriteCheck(Protocol receivedProtocol) throws Exception{
		// 찜 작업 생성
		FavoriteTask ft = new FavoriteTask();
		
		// 사용자에게서 계정정보 받음
		Account account = (Account) receivedProtocol.getObject();
		Tuple<Response, ArrayList<Favorite>> favoriteResponse = ft.findByAccount(account);
		
		// 응답과, 분류 분리
		Response response = favoriteResponse.getFirst();
		final ArrayList<Favorite> favoriteList = favoriteResponse.getSecond();
		
		// 계정정보를 찾았으면
		ArrayList<CollectedInfo> result = null;
		if(response.getResponseType() == ResponseType.SUCCEED) {
			// 그 계정의 찜목록이 존재한다면
			if(favoriteList != null && favoriteList.size() > 0) {
				result = getCheaperFavoriteOnly(favoriteList);
				if(result != null) {
					IOHandler.getInstance().log("[찜목록 비교 요청] 비교에 성공했습니다.");
					response = new Response(ResponseType.SUCCEED, "찜목록 비교 요청 : 비교에 성공했습니다.");
				}
				else {
					IOHandler.getInstance().log("[찜목록 비교 요청] 비교에 실패했거나 결과가 없습니다.");
					response = new Response(ResponseType.SUCCEED, "찜목록 비교 요청 : 비교에 실패했거나 결과가 없습니다.");
				}
			}
			else {
				IOHandler.getInstance().log("[찜목록 비교 요청] 해당 사용자의 찜목록이 없습니다.");
				response = new Response(ResponseType.SUCCEED, "찜목록 비교 요청 : 해당 사용자의 찜목록이 없습니다.");
			}
		}	
		else {
			IOHandler.getInstance().log("[찜목록 비교 요청] 해당 사용자의 계정정보를 찾을 수 없습니다.");
			response = new Response(ResponseType.FAILED, "찜목록 비교 요청 : 해당 사용자의 계정정보를 찾을 수 없습니다.");
		}
		
		// 분류 목록 포함시킨 프로토콜 생성
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.REQUEST_FAVORITE_CHECK, response, result); 
		
		// 결과를 전송함.
		sendOutputStream(sendProtocol);
	}
	
	// getCheaperFavoriteOnly
	private ArrayList<CollectedInfo> getCheaperFavoriteOnly(ArrayList<Favorite> favoriteList){
		ArrayList<Product> productList = new ArrayList<Product>();
		
		// 찜목록 탐색
		for(Favorite f : favoriteList) {
			// 해당 상품을 찾는다.
			ProductTask pt = new ProductTask();
			Product p = pt.getProductByName(f.getProductName());
			if(p != null) {
				// 파싱 대상 목록에 넣음
				productList.add(p);
			}
		}
		// 파싱 대상 상품 목록이 존재하면
		if(productList.size() > 0) {
			// 해당 상품들의 가장 최신 정보를 가져온다.
			ArrayList<Tuple<Product, CollectedInfo>> recent = getRecentProductTupleList(productList);
			ArrayList<CollectedInfo> result = new ArrayList<CollectedInfo>();
			
			// 상품들의 최신 목록 탐색
			if(recent != null) {
				for(Tuple<Product, CollectedInfo> t : recent) {
					for(Favorite f : favoriteList) {
						// 찜 목록에 있는 상품과 이름이 같으면, 가격 비교
//						IOHandler.getInstance().log("f.getProductName() : " + f.getProductName() + " VS t.getFirst().getName() : " + t.getFirst().getName());
						if(f.getProductName().equals(t.getFirst().getName())) {
							// 가격이 더 낮을때는, 결과 목록에 포함.
							if(f.getTargetPrice() > t.getSecond().getPrice()) {
								result.add(t.getSecond());
							}
							break;
						}
					}
				}
				return result.size() > 0 ? result : null;
			}
		}
		return null;
	}
	
	private void onUnknown(Protocol receivedProtocol) {
		try {
			IOHandler.getInstance().log("[SYSTEM] CODE 1031");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// 사용자에게서 받아온 계정 정보 획득 후 회원가입 시도
		Response response = new Response(ResponseType.UNKNOWN, "서버에서 알 수 없는 오류가 발생하였습니다 : CODE 1031");
		Protocol sendProtocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, response, null);
		
		// 결과를 전송함.
		try {
			sendOutputStream(sendProtocol);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
