package ru.miet.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.*; 

public class Server {
	public boolean go = true;
	static final int PORT = 343;
	Socket clientSocket = null;
	// серверный сокет
	ServerSocket serverSocket = null;
	private PrintWriter outMessage;
	  // входящее собщение
	public Scanner inMessage;
	String message;

	public Server() {
		// сокет клиента, это некий поток, который будет подключаться к серверу по
		// адресу и порту

 
		try {
			// создаём серверный сокет на определенном порту
			serverSocket = new ServerSocket(PORT);
			// запускаем бесконечный цикл
			
				// таким образом ждём подключений от сервера
				clientSocket = serverSocket.accept();
			   	outMessage = new PrintWriter(clientSocket.getOutputStream());
		    	inMessage = new Scanner(clientSocket.getInputStream());

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				// закрываем подключение
				clientSocket.close();
				serverSocket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	void recieve() {
		while (true) {
			try {
				if (clientSocket.getInputStream().available() != 0) {
					message = inMessage.nextLine();					
					int index = message.indexOf("TypeJSONLibrary");
					index = findNextInt(message, index);
					if (index<0)
						throw new JsonParseException("");
					
					new JSONthread(index, message, outMessage).start();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch(JsonParseException ex ) {
				  
				  recieve();
			}
		}
		  
	}
	int findNextInt(String S, int index){
		char ch;
		for (int i = index+2; i<S.length(); i++){
			ch = S.charAt(i);
			if(ch >= '0' && ch < '9')
				return Integer.parseInt(S.substring(i, i+1));			
			if(ch == ',' | ch == '{' | ch == '}')
				break;
		}
		return -1;
	}	
}
