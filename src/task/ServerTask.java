package task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import model.Account;
import network.Direction;
import network.LoginResult;
import network.Protocol;
import network.ProtocolType;
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
			ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			// 일단 프로토콜로 해석
			Protocol p = (Protocol) inputStream.readObject();
			
			// 프로토콜의 타입이 무엇인가?
			switch(p.getType()){
				case LOGIN:
					login(p);
					break;
				case REGISTER:
					break;
				case EVENT:
					break;
				case ERROR:
					break;
				default :
					break;
			}
			
			clientSocket.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// 소켓 다시한번 종료
		if(clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		IOHandler.getInstance().log("[SYSTEM] 담당일찐 스레드 종료됨");
		
	}
	
	private void login(Protocol p) throws Exception{
		// 로그인이면 계정 작업 생성
		AccountTask at = new AccountTask();
		
		// DB에 로그인 정보로 체크해봄
		Account account = (Account) p.getObject();
		LoginResult loginResult = at.tryLogin(account);
		Protocol protocol = new Protocol(ProtocolType.LOGIN, Direction.TO_CLIENT, loginResult);
		
		// 결과를 전송함.
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectOutputStream.writeObject(protocol);
		objectOutputStream.flush();
	}

}
