package utility;

import java.util.Date;
import java.util.Scanner;

public class IOHandler {
	
	// �̱��� ����
	private static IOHandler _instance;
	
	// IOHandler ��� �� IOHandler.getInstance().�޼ҵ�� ���� ����ϸ� ��.
	public static IOHandler getInstance()
	{
		if(_instance == null)
			_instance = new IOHandler();
		return _instance;
	}
	
	// �ܼ� or �ؽ�Ʈ ���Ͽ� �α�
	public void log(String s) {
		// [��¥+�ð�+����ڸ�]+������ �̷������� �α��ϰ� �� ������
		System.out.println("[" + new Date() + ".Server]" + s);
	}
	
	public int getIntByUser() {
		try {
			Scanner scn = new Scanner(System.in);
			System.out.print(">> ");
			return scn.nextInt();			
		}
		catch(Exception e) {
			log(e.getMessage());
			return -987654321;
		}
	}
	
	public String getLineByUser(String msg) {
		try {
			if(msg != null) {
				System.out.println(msg);
			}
			Scanner scn = new Scanner(System.in);
			System.out.print(">> ");
			return scn.nextLine();			
		}
		catch(Exception e) {
			log(e.getMessage());
			return null;
		}
	}
}
