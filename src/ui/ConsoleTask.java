package ui;

import java.util.ArrayList;

import db.CollectedInfoManager;
import model.CollectedInfo;
import model.Product;
import task.CSVReader;
import task.CollectedInfoTask;
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
		
		// 원하는 상품정보 선택
		Product targetProduct = selectProduct(productList);
		
		// 웹 파싱하여 DB에 추가/업데이트
		CollectedInfoTask cit = new CollectedInfoTask();
		boolean isSucceed = cit.collect(targetProduct);
		
		if(isSucceed) {
			IOHandler.getInstance().log("[SYSTEM][수집정보 업데이트]상품 " + targetProduct.getName() + "의 수집정보가 갱신되었습니다.");
		}
		else {
			IOHandler.getInstance().log("[SYSTEM][수집정보 업데이트]상품 " + targetProduct.getName() + "의 수집정보가 갱신되지 않았습니다.(갱신하지 않았거나, 갱신 실패함)");
		}
	}
	
	// 수동으로 상품정보를 찾는 private 메소드
	private ArrayList<Product> searchProducts() {
		ProductTask pt = new ProductTask();
		String userInput = IOHandler.getInstance().getLineByUser("[SYSTEM]DB에서 상품정보를 찾습니다. 검색어를 입력하세요.");
		
		return pt.search(userInput);
	}
	
	// 수동으로 상품정보 목록을 보여주고 선택하는 private 메소드
	private Product selectProduct(ArrayList<Product> productList) {
		int cnt = 0;
		for(Product p : productList) {
			System.out.println("\t" + cnt++ + ". " + p.getName());
		}
		int selected = IOHandler.getInstance().getIntByUser("[SYSTEM]원하는 상품의 인덱스를 입력하세요.");
		return productList.get(selected);
	}
	
	// 수동으로 CSV 폴더 경로를 주면 내부의 모든 CSV를 파싱하여 DB를 업데이트하는 메소드
	public void manualDumpCSV() {
		CSVReader cr = new CSVReader();
		String userInput = IOHandler.getInstance().getLineByUser("경로를 입력하세요.");
		boolean isSucceed = cr.dumpCSV(userInput);
		if(isSucceed) {
			IOHandler.getInstance().log("[SYSTEM]경로 " + userInput + "내 모든 CSV가 DB에 갱신되었습니다.");
		}
		else {
			IOHandler.getInstance().log("[SYSTEM]경로 " + userInput + "내 CSV를 갱신하는 도중 오류가 발생하였습니다.");
		}
	}
	
	// 수동으로 DB에서 상품정보를 선택, 해당 상품에 대한 역대 수집정보를 DB에서 모두 조회하는 메소드
	public void searchCollectedInfo() {
		// DB에서 상품정보 검색
		ArrayList<Product> productList = searchProducts();
		if(productList == null) {
			IOHandler.getInstance().log("[SYSTEM]검색 결과가 없습니다.");
			return;
		}
		
		// 원하는 상품정보 선택
		Product targetProduct = selectProduct(productList);
		
		// DB에서 해당되는 수집정보 조회
		CollectedInfoManager cim = new CollectedInfoManager();
		try {
			ArrayList<CollectedInfo> collectedInfoList =  cim.findByProductName(targetProduct.getName());
			if(collectedInfoList == null) {
				IOHandler.getInstance().log("[SYSTEM]해당되는 상품의 수집 조회가 없습니다.");
				return;
			}
			
			// 목록 출력
			IOHandler.getInstance().log("[SYSTEM]해당되는 상품의 수집정보 목록입니다.");
			int cnt = 0;
			for(CollectedInfo c : collectedInfoList) {
				System.out.println("\t" + cnt++ + ". " +
						c.getProductName() + ", " +
						c.getCollectedDate().toString() + ", " +
						c.getPrice() + ", " +
						c.getUrl() + ", " + 
						String.valueOf(c.getHits()) + ", " +
						c.getThumbnail()
						);
			}
			
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("ConsoleTask.searchCollectedInfo", e);
		}
	}
}
