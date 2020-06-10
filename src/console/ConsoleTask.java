package console;

import java.util.ArrayList;

import model.Account;
import model.BigCategory;
import model.Category;
import model.CollectedInfo;
import model.Favorite;
import model.Product;
import model.Tuple;
import network.Response;
import network.ResponseType;
import task.AccountTask;
import task.BigCategoryTask;
import task.CSVReader;
import task.CategoryTask;
import task.CollectedInfoTask;
import task.FavoriteTask;
import task.ProductTask;
import utility.IOHandler;

public class ConsoleTask {
	
	// 수동으로 DB에서 상품정보를 선택, 해당 상품에 관한 정보를 실시간 웹 파싱해서 DB에 업데이트함.
	public void manualCollectInfo() {
		// DB에서 상품정보 검색
		ArrayList<Product> productList = searchProducts();
		if(productList == null) {
			IOHandler.getInstance().log("[SYSTEM]검색 결과가 없습니다.");
			return;
		}
		
		// 결과가 하나라면 자동으로 첫번째 객체로 진행, 결과가 여러개면 사용자에게 선택시킴. 
		Product targetProduct = (Product) selectObject(productList);
		
		// 웹 파싱하여 DB에 추가/업데이트
		CollectedInfoTask cit = new CollectedInfoTask();
		Response response = cit.collect(targetProduct);
		
		IOHandler.getInstance().log("[SYSTEM][수집정보 업데이트]상품 " + targetProduct.getName() + " 업데이트 결과 : " + response.getMessage());
	}
	
	// 수동으로 DB에서 상품정보를 선택, 해당 상품에 대한 역대 수집정보를 DB에서 모두 조회하는 메소드
	public void checkCollectedInfo() {
		// DB에서 상품정보 검색
		ArrayList<Product> productList = searchProducts();
		if(productList == null) {
			IOHandler.getInstance().log("[SYSTEM]검색 결과가 없습니다.");
			return;
		}
		
		// 결과가 하나라면 자동으로 첫번째 객체로 진행, 결과가 여러개면 사용자에게 선택시킴.
		Product targetProduct = (Product) selectObject(productList);
		
		// DB에서 해당되는 수집정보 조회
		CollectedInfoTask cit = new CollectedInfoTask();
		Tuple<Response, ArrayList<CollectedInfo>> result = cit.findByProduct(targetProduct);
		
		ArrayList<CollectedInfo> collectedInfoList = result.getSecond();
		
		if(collectedInfoList == null) {
			IOHandler.getInstance().log("[SYSTEM]해당되는 상품의 수집 조회가 없습니다.");
			return;
		}
	
		// 목록 출력
		IOHandler.getInstance().log("[SYSTEM]해당되는 상품의 수집정보 목록입니다.");
		printList(collectedInfoList);
	}
	
	// 수동으로 CSV 폴더 경로를 주면 내부의 모든 CSV를 파싱하여 DB를 업데이트하는 메소드
	public void manualDumpCSV() {
		CSVReader cr = new CSVReader();
		String userInput = IOHandler.getInstance().getLineByUser("경로를 입력하세요.");
		boolean isSucceed = cr.dumpCSV(userInput);
		if(isSucceed) {
			IOHandler.getInstance().log("[SYSTEM]경로 " + userInput + " 내의 모든 CSV가 DB에 갱신되었습니다.");
		}
		else {
			IOHandler.getInstance().log("[SYSTEM]경로 " + userInput + " 내의 CSV를 갱신하는 도중 오류가 발생하였습니다.");
		}
	}
	
	// 수동 계정 추가
	public void manualAddAccount() {
		String accountId = IOHandler.getInstance().getLineByUser("[SYSTEM]추가할 계정의 ID를 입력하세요.");
		String accountPw = IOHandler.getInstance().getLineByUser("[SYSTEM]추가할 계정의 PW를 입력하세요.");
		Account account = new Account(accountId, accountPw);
		
		AccountTask at = new AccountTask();
		at.register(account);
	}
	
	// 수동 계정 조회
	public void manualCheckAccount() {
		String accountId = IOHandler.getInstance().getLineByUser("[SYSTEM]조회할 계정의 ID를 입력하세요.");
		
		AccountTask at = new AccountTask();
		ArrayList<Account> accountList = at.searchById(accountId);
		if(accountList == null) {
			IOHandler.getInstance().log("[SYSTEM]계정 검색 결과가 없습니다.");
			return;
		}
		
		Account selected = (Account) selectObject(accountList);
		
		System.out.println("[SYSTEM] 조회된 계정 : " + selected.toString());
		return ;
	}
	
	// 수동 품목정보 조회. 대품목정보를 선택하면 그 하위 품목정보 모두 보여줌.
	public void manualCheckCategory() {
		// 대품목정보 모두 가져옴
		BigCategoryTask bct = new BigCategoryTask();
		Tuple<Response, ArrayList<BigCategory>> bigCategoryReceived = bct.getAllBigCategory();
		
		ArrayList<BigCategory> bigCategoryList = bigCategoryReceived.getSecond();
		
		// 대품목정보 중 하나 선택
		BigCategory selected = (BigCategory) selectObject(bigCategoryList);
		
		// 해당 대품목정보에 속하는 품목정보 다 가져옴
		CategoryTask ct = new CategoryTask();
		Tuple<Response, ArrayList<Category>> categoryReceived = ct.findByBigCategory(selected);
		ArrayList<Category> categoryList = categoryReceived.getSecond();
		
		if(categoryList == null) {
			IOHandler.getInstance().log("[SYSTEM]품목정보 조회 결과가 없습니다.");
			return;
		}
		IOHandler.getInstance().log("[SYSTEM]품목정보 목록입니다.");
		printList(categoryList);
	}
	
	// 수동 찜목록 추가
	public void manualAddFavorites() {
		// 계정 탐색
		IOHandler.getInstance().log("[SYSTEM]찜을 추가할 계정을 입력 및 선택해주세요.");
		ArrayList<Account> accountList = searchAccounts();
		if(accountList == null) {
			IOHandler.getInstance().log("[SYSTEM]사용자 조회 결과가 없습니다.");
			return;
		}
		
		// 사용자에게 어떤 계정을 선택할건지 묻는다.
		Account account = (Account) selectObject(accountList);
		
		// 상품정보 탐색
		IOHandler.getInstance().log("[SYSTEM]상품을 입력 및 선택해주세요.");
		ArrayList<Product> productList = searchProducts();
		if(productList == null) {
			IOHandler.getInstance().log("[SYSTEM]상품 조회 결과가 없습니다.");
			return;
		}
		
		// 사용자에게 어떤 상품을 추가할건지 묻는다.
		Product product = (Product) selectObject(productList);
		
		// 사용자에게 목표금액을 물어본다.
		double targetPrice = IOHandler.getInstance().getIntByUser("[SYSTEM]목표 금액을 설정해주세요.");
		
		FavoriteTask ft = new FavoriteTask();
		Favorite f = new Favorite(account.getId(), product.getName(), targetPrice);
		ft.addFavorite(f, true);
		
	}
	
	// 수동 찜목록 조회
	public void manualCheckFavorites() {
		// 계정 탐색
		ArrayList<Account> accountList = searchAccounts();
		if(accountList == null) {
			IOHandler.getInstance().log("[SYSTEM]사용자 조회 결과가 없습니다.");
			return;
		}
		
		// 사용자에게 어떤 계정을 선택할건지 묻는다.
		Account selected = (Account) selectObject(accountList);
		
		FavoriteTask ft = new FavoriteTask();
		Tuple<Response, ArrayList<Favorite>> received = ft.findByAccount(selected);
		
		ArrayList<Favorite> favoriteList = received.getSecond();
		
		if(favoriteList == null) {
			IOHandler.getInstance().log("[SYSTEM]찜 목록 조회 결과가 없습니다.");
			return;
		}
		IOHandler.getInstance().log("[SYSTEM]찜 목록입니다.");
		printList(favoriteList);
	}
	
	// ---------------------------------------------------
	
	// 수동으로 상품정보를 찾는 private 메소드.
	private ArrayList<Product> searchProducts() {
		String searchWord = IOHandler.getInstance().getLineByUser("[SYSTEM]DB에서 상품정보를 찾습니다. 검색어를 입력하세요.");
		
		ProductTask pt = new ProductTask();
		return pt.searchByProductName(searchWord).getSecond();
	}
	
	// 수동으로 사용자 정보를 찾는 private 메소드
	private ArrayList<Account> searchAccounts(){
		String accountId = IOHandler.getInstance().getLineByUser("[SYSTEM]조회할 계정의 ID를 입력하세요.");
		
		AccountTask at = new AccountTask();
		return at.searchById(accountId);
	}
	
	// 수동으로 목록을 보여주고 선택하는 private 메소드
	private Object selectObject(ArrayList<?> objectList) {
		// 개수가 하나라면 자동으로 첫번째 항목 반환
		if(objectList.size() == 1) {
			return objectList.get(0);
		}
		
		int cnt = 0;
		for(Object o : objectList) {
			System.out.println("\t" + cnt++ + ". " + o.toString());
		}
		int selected = IOHandler.getInstance().getIntByUser("[SYSTEM]원하는 항목의 인덱스를 입력하세요.");
		return objectList.get(selected);
	}
	
	// 목록을 출력하는 메소드
	private void printList(ArrayList<?> objectList) {
		int cnt = 0;
		for(Object c : objectList) {
			System.out.println("\t" + cnt++ + ". " +c.toString());
			}
	}
}
