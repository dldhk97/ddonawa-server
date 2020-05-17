package task;

import java.util.ArrayList;
import java.util.Arrays;

import db.AccountManager;
import model.Account;
import network.LoginResult;
import utility.IOHandler;

public class AccountTask {
	// 반환형 만들고 메시지출력은 ConsoleTask에서 해라
	public boolean register(Account account) {
		AccountManager am = new AccountManager();
		try {
			ArrayList<String> keys = new ArrayList<String>(Arrays.asList(
					account.getId()
					));
			
			if(am.findByKey(keys) != null) {
				IOHandler.getInstance().log("해당 아이디는 중복입니다.");
				return false;
			}
			
			if(am.insert(account) > 0) {
				IOHandler.getInstance().log("계정이 등록되었습니다.");
				return true;
			}
			else {
				IOHandler.getInstance().log("계정이 등록에 실패하였습니다.");
				return false;
			}
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("[LoginManager.register]", e);
		}
		
		return false;
	}
	
	// 반환형 만들고 메시지출력은 ConsoleTask에서 해라
	public boolean checkAccount(String accountId) {
		AccountManager am = new AccountManager();
		try {
			ArrayList<String> keys = new ArrayList<String>(Arrays.asList(
					accountId
					));
			
			Account received = (Account) am.findByKey(keys);
			if(received != null) {
				IOHandler.getInstance().log("계정 조회됨. ID : " + received.getId() + ", PW : " + received.getPw());
				return true;
			}
			else {
				IOHandler.getInstance().log("계정 조회되지 않음");
				return false;
			}
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("[LoginManager.register]", e);
		}
		
		return false;
	}
	
	// 반환형 만들고 메시지출력은 ConsoleTask에서 해라
	public LoginResult tryLogin(Account account) throws Exception{
		AccountManager am = new AccountManager();
		try {
			Account searchedAccount = (Account)am.findByKey(new ArrayList<String>(Arrays.asList(account.getId())));
			if(searchedAccount == null) {
				// 아이디가 없는 경우
				IOHandler.getInstance().log("해당되는 아이디가 없습니다.");
				return LoginResult.ID_NOT_FOUND;
			}
			else {
				// 아이디가 있는 경우
				IOHandler.getInstance().log("해당되는 아이디가 있습니다.");
				if(searchedAccount.getPw().equals(account.getPw())) {
					// 비밀번호가 일치한 경우
					return LoginResult.SUCCEED;
				}
				else {
					// 비밀번호가 틀린 경우
					return LoginResult.WRONG_PW;
				}
			}
		} catch (Exception e) {
			IOHandler.getInstance().log("[LoginManager.tryLogin]", e);
			return LoginResult.ERROR;
		}
	}
}
