package task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import utility.IOHandler;

public class ServerTask implements Runnable{
	
	private final Socket clientSocket;
	
	public ServerTask(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		IOHandler.getInstance().log("[SYSTEM] 담당일찐 스레드 생성 완료");
		
		String s = "hihi";
		
		try {
			OutputStream output = clientSocket.getOutputStream();
			output.write(s.getBytes());
			
			clientSocket.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		IOHandler.getInstance().log("[SYSTEM] 담당일찐 스레드 종료됨");
		
	}

}
