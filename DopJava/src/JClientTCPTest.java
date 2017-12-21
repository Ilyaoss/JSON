import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class JClientTCPTest {

	public static int nmas = 1; // ���������� ����� (�������� ��� ��������)
	public static int ndata = 5; // ���������� ���� � �������
	public static int b = 100; // ��� ������ � ������
	public static int library = 0;
	private static final int SERVER_PORT = 343;
	private static String SERVER_HOST = "localhost";
	JSONObject resultJson = new JSONObject(); // �������� ������
	JSONArray resultList = new JSONArray(); // ���� ��� ������� ����
	Socket socket;
	private Scanner inMessage;
	private PrintWriter socketOut;

	public static void main(String[] args)
			throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
		JClientTCPTest tcpTest = new JClientTCPTest();
		tcpTest.test();
		
	}

	public void test() {
		Sender sender = new Sender();
		sender.start();
		
	}

	public String randomMes(int nmas, int ndat, int nbyte, JSONObject obj, JSONArray list, int libr) {
		String s = "";
		// 1�� = 1024�� = 1024*1024���� = 2�����*512*1024 = 1 char*512*1024
		int size = 512 *1024;
		char[] filledArray = new char[size];
		// ��������� ��������� (���������� ��� ����������)
		java.util.Arrays.fill(filledArray, '1');
		String filledString = new String(filledArray); // ������ �� 1 �� �� ������
		obj.put("TypeJSONLibrary", libr); // ���������� � �������

		// ������� ���� ��� ������ � char
		// ���� ������ � ������ ����� �� ����� ���� �������� ��������
		// � �� �������� �����, ��� ��� �������� �� 2, �.�. ��� ������ char
		// �������� 2 �����(16 ���)
		// ������� ����� ����� � ���������� �� ��� �����
		int wordLenth = size / ((nmas * ndat + nmas) * 2);
		// ������� ����� � 0 �� wordLenth-1
		String word = filledString.substring(0, wordLenth - 1);
		// ���� �� i - ������� ��������
		// ���� �� j - ������� ����� � �������(����)
		// System.out.println("" + word);
		for (int j = 0; j < ndat; j++) {
			list.add(word); // ��������� �����
			// System.out.println("���� ����");
		}
		for (int i = 0; i < nmas; i++) {
			
			/*for(int k = 0; k < list.size();++k)
			{
				System.out.println(list.get(k));
			}*/
			obj.put(word, list); // ������ � ������
			//list.clear(); // ������ ����
		}
		
		s = obj.toString(); // ����������� � string
		return s;
	}

	class Sender extends Thread {
		public void run() {
			try {

				socket = new Socket(SERVER_HOST, SERVER_PORT);
				try {
					inMessage = new Scanner(socket.getInputStream());
					socketOut = new PrintWriter(socket.getOutputStream(), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				new Thread(new Runnable() {
					public void run() {
						try {
							// ����������� ����
							while (true) {
								// ���� ���� �������� ���������
								if (socket.getInputStream().available() != 0) {
									// ��������� ���
									String inMes = inMessage.nextLine();
									System.out.println(inMes);
								}
							}
						} catch (Exception e) {

						}
					}
				}).start();
				String message;

				// message = "{\"task\":\"JTestTask\",\"message\":\"test" + "\"}";
				message = randomMes(nmas, ndata, b, resultJson, resultList, library);
				System.out.println(resultJson.toString());
				// InputStream inStream = socket.getInputStream();
				
				for (int i = 0; i < 1; ++i) 
				/*while(true)*/{
					socketOut.println(message);
					socketOut.flush();
					System.out.println("Send.");
					//Thread.sleep(100);
				}
			} catch (IOException ex) {
				// Logger.getLogger(JClientTCPTest.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					socket.close();
				} catch (IOException ex) {

				}
			}
		}
	}
}