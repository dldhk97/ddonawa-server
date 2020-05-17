package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Account;

public class AccountManager extends DBManager{
	
	@Override
	protected  ArrayList<String> getTableColumnsAll(){
		return new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_ACCOUNT_COLUMN_ID.toString(),
				DBInfo.TABLE_ACCOUNT_COLUMN_PW.toString()
				));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_ACCOUNT.toString() + "` WHERE `" +
				DBInfo.TABLE_ACCOUNT_COLUMN_ID.toString() + "` = '" + keyValues.get(0) + "'";
	}
	@Override
	protected Object getModel(ArrayList<ArrayList<String>> received) {
		for(ArrayList<String> row : received) {
			return new Account(row.get(0), row.get(1));
		}
		return null;
	}
	@Override
	protected ArrayList<String> modelToStringArray(Object object){
		Account account = (Account)object;
		return new ArrayList<>(Arrays.asList(
				account.getId(), 
				account.getPw()
				));
	}
	@Override
	protected String getTableName() {
		return DBInfo.TABLE_ACCOUNT.toString();
	}
	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object){
		Account account = (Account)object;
		return new ArrayList<>(Arrays.asList(
				account.getId()
				));
	}

}
