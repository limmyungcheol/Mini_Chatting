package chatting;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class ChatSever extends Thread{
	static Hashtable<String, PrintWriter> map = new Hashtable<String, PrintWriter>();
	BufferedReader br;
	String userId;
	public ChatSever(String userId, BufferedReader br) {
		this.userId = userId;
		this.br = br;
		
		sendmessage (userId + "님이 입장 하였습니다");
	}
	
	void sendmessage (String line) {
		
		// 전달 받은 메세지를 모든 소켓에 뿌린다.
		Enumeration<String> keys = map.keys();
		while (keys.hasMoreElements()) {
			String idKey = keys.nextElement();
			PrintWriter pw = map.get(idKey);
			pw.println(line);
			pw.flush();
		}
	}
	@Override
	public void run() {
		while (true) {
			try {
				String line = br.readLine();
				sendmessage(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// 서버 = ServerSocket -> accept -> Socket
		BufferedReader br = null;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(7777);
			System.out.println("클라이언트 연결을 대기중 .....");
			
			// 대기하기 유지
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("클라이언트 연결 되었음");
				//클라이언트 메세지 받기
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String userId = br.readLine();
				//System.out.println(userId + "님이 접속하셨습니다");
				PrintWriter pw = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
				
				
				map.put(userId, pw);
				
				// 브로드 케스트가 준비 됨
				ChatSever server = new ChatSever(userId, br);
				server.start();
				}
		} catch (IOException e) {
			System.out.println("채팅 중 오류 발생!!");
			
		}
		
	}

}
