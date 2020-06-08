package task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import model.Account;
import model.Product;
import network.Direction;
import network.EventType;
import network.Protocol;
import network.ProtocolType;
import network.Response;
import network.ResponseType;
import utility.IOHandler;
import utility.Tuple;

public class ServerTask implements Runnable{
	
	private final Socket clientSocket;
	
	public ServerTask(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		IOHandler.getInstance().log("[담당일찐 스레드] 스레드 생성 완료");
		
		String s = "hihi";
		
		try {
			ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			// 일단 프로토콜로 해석
			Protocol p = (Protocol) inputStream.readObject();
			
			// 프로토콜의 타입이 무엇인가?
			switch(p.getType()){
				case LOGIN:
					onLogin(p);
					break;
				case REGISTER:
					onRegister(p);
					break;
				case EVENT:
					onEvent(p);
					break;
				case ERROR:
					break;
				default :
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
		IOHandler.getInstance().log("[담당일찐 스레드] 스레드 종료됨");
		
	}
	
	private void onLogin(Protocol p) throws Exception{
		// 로그인이면 계정 작업 생성
		AccountTask at = new AccountTask();
		
		// 사용자에게서 받아온 계정 정보 획득 후 로그인 시도
		Account account = (Account) p.getObject();
		Response response = at.tryLogin(account);
		Protocol protocol = new Protocol(ProtocolType.LOGIN, Direction.TO_CLIENT, response, null);
		
		// 결과를 전송함.
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectOutputStream.writeObject(protocol);
		objectOutputStream.flush();
	}
	
	private void onRegister(Protocol p) throws Exception{
		// 회원가입 계정 작업 생성
		AccountTask at = new AccountTask();
		
		// 사용자에게서 받아온 계정 정보 획득 후 회원가입 시도
		Account account = (Account) p.getObject();
		Response response = at.register(account);
		Protocol protocol = new Protocol(ProtocolType.REGISTER, Direction.TO_CLIENT, response, null);
		
		// 결과를 전송함.
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectOutputStream.writeObject(protocol);
		objectOutputStream.flush();
	}
	
	// 이벤트는 경우의 수가 많기 때문에 한번 더 getEventType으로 케이스 분기함.
	private void onEvent(Protocol p) throws Exception{
		switch(p.getEventType()) {
			case GET_BIG_CATEGORY:
				break;
			case GET_CATEGORY:
				break;
			case SEARCH:
				onSearch(p);
				IOHandler.getInstance().log("[담당일찐 스레드] 검색 결과 반환 완료");
				break;
			case GET_PRODUCT_DETAIL:
				break;
			default:
				break;
		}
	}
	
	private void onSearch(Protocol p) throws Exception {
		// 상품 작업 생성
		ProductTask pt = new ProductTask();
		
		// 사용자에게서 받아온 검색어 획득 후 검색
		String searchWord = (String) p.getObject();
		Tuple<Response, ArrayList<Product>> result = pt.searchByProductName(searchWord);
		
		// 응답 및 검색결과 받아옴
		Response response = result.getFirst();
		ArrayList<Product> searchResult = result.getSecond();
		
		// 프로토콜 생성
		Protocol protocol = new Protocol(ProtocolType.EVENT, Direction.TO_CLIENT, EventType.SEARCH, response, (Object)searchResult);
		
		// 결과를 전송함.
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectOutputStream.writeObject(protocol);
		objectOutputStream.flush();
	}

}
