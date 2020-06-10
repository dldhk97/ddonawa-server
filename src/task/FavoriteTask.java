package task;

import java.util.ArrayList;

import db.FavoriteManager;
import model.Account;
import model.Favorite;
import model.Product;
import model.Tuple;
import network.Response;
import network.ResponseType;
import utility.IOHandler;

public class FavoriteTask {
	public Response addFavorite(Favorite favorite) {
		try {
			// 계정 존재하는지 체크
			AccountTask at = new AccountTask();
			ArrayList<Account> accountList = at.searchById(favorite.getAccountId());
			
			if(accountList == null) {
				IOHandler.getInstance().log("찜 목록에 추가할 계정이 존재하지 않습니다.");
				return new Response(ResponseType.FAILED, "찜 목록에 추가할 계정이 존재하지 않습니다.");
			}
			
			// 상품정보 존재하는지 체크
			ProductTask pt = new ProductTask();
			Tuple<Response, ArrayList<Product>> result = pt.searchByProductName(favorite.getProductName());
			
			ArrayList<Product> searchResult = result.getSecond();
			
			if(searchResult == null) {
				IOHandler.getInstance().log("찜 목록에 추가할 상품이 존재하지 않습니다.");
				return new Response(ResponseType.FAILED, "찜 목록에 추가할 상품이 존재하지 않습니다.");
			}
			
			// 찜목록에 추가
			FavoriteManager fm = new FavoriteManager();
			int cnt = fm.insert(favorite);
			
			if(cnt > 0) {
				IOHandler.getInstance().log("찜 추가에 성공하였습니다.");
				return new Response(ResponseType.SUCCEED, "찜 추가에 성공했습니다.");
			}
			else {
				IOHandler.getInstance().log("찜 추가에 실패했습니다.");
				return new Response(ResponseType.FAILED, "찜 추가에 실패했습니다.");
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("FavoriteTask.addFavorite", e);
		}
		
		return new Response(ResponseType.ERROR, "찜 추가 중 서버에서 알 수 없는 오류가 발생했습니다.");
	}
	
	public Response deleteFavorite(Favorite favorite) {
		try {
			// 찜목록에 추가
			FavoriteManager fm = new FavoriteManager();
			int cnt = fm.delete(favorite);
			
			if(cnt > 0) {
				IOHandler.getInstance().log("찜 삭제에 성공하였습니다.");
				return new Response(ResponseType.SUCCEED, "찜 삭제에 성공했습니다.");
			}
			else {
				IOHandler.getInstance().log("찜 삭제에 실패했습니다.");
				return new Response(ResponseType.FAILED, "찜 삭제에 실패했습니다.");
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("FavoriteTask.addFavorite", e);
		}
		
		return new Response(ResponseType.ERROR, "찜 삭제 중 서버에서 알 수 없는 오류가 발생했습니다.");
	}
	
	public Tuple<Response, ArrayList<Favorite>> findByAccount(Account account){
		Response response = null;
		try {
			// 문자열 정규화 등 선처리. 현재는 별도의 처리 없이 그대로 DB에 SELECT함.
			
			// SQL에 검색
			FavoriteManager fm = new FavoriteManager();
			ArrayList<Favorite> favoriteList = fm.findByAccountId(account.getId());
			
			// 결과 반환
			response = new Response(ResponseType.SUCCEED, "찜 조회에 성공했습니다."); 
			Tuple<Response, ArrayList<Favorite>> result = new Tuple<Response, ArrayList<Favorite>>(response, favoriteList);
			return result;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("FavoriteTask.findByAccount", e);
			response = new Response(ResponseType.ERROR, "찜 조회 중 서버에서 오류가 발생했습니다."); 
		}
		return new Tuple<Response, ArrayList<Favorite>>(response, null);
	}
}
