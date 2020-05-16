package task;

import java.util.ArrayList;
import java.util.Arrays;

import db.AccountManager;
import enums.LoginResult;
import model.Account;
import utility.IOHandler;

public class LoginTask {
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
