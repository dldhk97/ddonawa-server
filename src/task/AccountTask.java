package task;

import java.util.ArrayList;

import db.AccountManager;
import model.Account;
import network.Response;
import network.ResponseType;
import utility.IOHandler;

public class AccountTask {
	// 반환형 만들고 메시지출력은 ConsoleTask에서 해라
	public Response register(Account account) {
		AccountManager am = new AccountManager();
		try {
			if(isInvalidAccountInfo(account)) {
				IOHandler.getInstance().log("ID 혹은 PW가 글러먹었습니다.");
				return new Response(ResponseType.FAILED, "ID 혹은 PW가 글러먹었습니다.");
			}
			ArrayList<Account> received = searchById(account.getId());
			
			if(received != null) {
				IOHandler.getInstance().log("해당 아이디는 중복입니다.");
				return new Response(ResponseType.FAILED, "해당 아이디는 중복입니다.");
			}
			
			if(am.insert(account) > 0) {
				IOHandler.getInstance().log("계정이 등록되었습니다.");
				return new Response(ResponseType.SUCCEED, "계정이 등록되었습니다.");
			}
			else {
				IOHandler.getInstance().log("계정 등록에 실패하였습니다.");
				return new Response(ResponseType.FAILED, "계정이 등록에 실패하였습니다.");
			}
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("[LoginManager.register]", e);
		}
		
		return new Response(ResponseType.UNKNOWN, "알 수 없는 오류가 발생했습니다.");
	}
	
	public ArrayList<Account> searchById(String id){
		try {
			AccountManager am = new AccountManager();
			ArrayList<Account> searchResult = am.searchByAccountId(id);
			
			// 결과 반환
			return searchResult;
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("AccountTask.search", e);
		}
		return null;
	}
	
	// 반환형 만들고 메시지출력은 ConsoleTask에서 해라
	public Response tryLogin(Account account) throws Exception{
		try {
			ArrayList<Account> received = searchById(account.getId());
			
			if(received == null) {
				// 아이디가 없는 경우
				IOHandler.getInstance().log("해당되는 아이디가 없습니다.");
				return new Response(ResponseType.FAILED, "해당되는 아이디가 없습니다.");
			}
			else {
				// 아이디가 있는 경우
				Account dbAccount = received.get(0);
				if(dbAccount.getPw().equals(account.getPw())) {
					// 비밀번호가 일치한 경우
					return new Response(ResponseType.SUCCEED, "로그인 성공");
				}
				else {
					// 비밀번호가 틀린 경우
					return new Response(ResponseType.FAILED, "비밀번호가 틀렸습니다.");
				}
			}
		} catch (Exception e) {
			IOHandler.getInstance().log("[LoginManager.tryLogin]", e);
		}
		return new Response(ResponseType.UNKNOWN, "알 수 없는 오류가 발생했습니다.");
	}
	
	// ------------------- 계정 생성시 무결성 체크 ----------------------
	
	private boolean isInvalidAccountInfo(Account account) {
		String id = account.getId();
		if(id.equals("admin") || id.equals("administrator") || id.equals("superuser") ||
				id.equals("root") || id.equals("server") || id.equals("client") || id.equals("user")) {
			return true;	// 어드민 사용 불가
		}
		else if(id.length() < 4) {
			return true;	// 그래도 4글자는 되야지
		}
		else if(id.matches(".*[^xfe0-9a-zA-Z\\\\s].*")){
			return true;	// 특수문자, 공백, 한글 사용 불가
		}
		
		String pw = account.getPw();
		if(pw.contains(id)) {
			return true;	// 비밀번호 안에 아이디가 포함 불가
		}
		else if(pw.length() < 4) {
			return true;
		}
		else if(pw.matches(".*[^xfe0-9a-zA-Z\\\\\\\\s!@#$%^&*(),.?\\\\\\\":{}|<>\\=\\-\\+\\_\\[\\]].*")){
			return true;	// 비밀번호는 특수문자 가능. 근데 세미콜론이랑 따옴표는 안되게 함.
		}
		
		return false;
	}
	
}
