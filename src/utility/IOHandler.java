package utility;

import java.util.Date;

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
}
