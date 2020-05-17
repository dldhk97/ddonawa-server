package task;

import java.util.ArrayList;

import db.FavoriteManager;
import model.Account;
import model.Favorite;
import model.Product;
import utility.IOHandler;

public class FavoriteTask {
	public void addFavorite(Account account, Product product, double targetPrice) {
		try {
			// 계정 존재하는지 체크
			AccountTask at = new AccountTask();
			ArrayList<Account> accountList = at.searchById(account.getId());
			
			if(accountList == null) {
				IOHandler.getInstance().log("찜 목록에 추가할 계정이 존재하지 않습니다.");
				return;
			}
			
			// 상품정보 존재하는지 체크
			ProductTask pt = new ProductTask();
			ArrayList<Product> productList = pt.searchByProductName(product.getName());
			
			if(productList == null) {
				IOHandler.getInstance().log("찜 목록에 추가할 상품이 존재하지 않습니다.");
				return;
			}
			
			// 찜목록에 추가
			FavoriteManager fm = new FavoriteManager();
			int cnt = fm.insert(new Favorite(accountList.get(0).getId(), productList.get(0).getName(), targetPrice));
			
			if(cnt > 0) {
				IOHandler.getInstance().log("찜 추가에 성공하였습니다.");
			}
			else {
				IOHandler.getInstance().log("찜 추가에 실패했습니다.");
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("FavoriteTask.addFavorite", e);
		}
	}
	
	public ArrayList<Favorite> findByAccount(Account account){
		try {
			// 문자열 정규화 등 선처리. 현재는 별도의 처리 없이 그대로 DB에 SELECT함.
			
			// SQL에 검색
			FavoriteManager fm = new FavoriteManager();
			ArrayList<Favorite> received = fm.findByAccountId(account.getId());
			
			// 결과 반환
			return received;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("FavoriteTask.findByAccount", e);
		}
		return null;
	}
}
