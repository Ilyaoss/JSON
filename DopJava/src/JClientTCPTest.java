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

	public static int nmas = 1; // количество узлов (разделов или массивов)
	public static int ndata = 5; // количество слов в разделе
	public static int b = 100; // вес пакета в байтах
	public static int library = 0;
	private static final int SERVER_PORT = 343;
	private static String SERVER_HOST = "localhost";
	JSONObject resultJson = new JSONObject(); // итоговый объект
	JSONArray resultList = new JSONArray(); // лист для каждого узла
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
		// 1мб = 1024кб = 1024*1024байт = 2байта*512*1024 = 1 char*512*1024
		int size = 512 *1024;
		char[] filledArray = new char[size];
		// заполняем единицами (переделать под библиотеки)
		java.util.Arrays.fill(filledArray, '1');
		String filledString = new String(filledArray); // строка на 1 мб из единиц
		obj.put("TypeJSONLibrary", libr); // библиотека с номером

		// сколько байт под строку в char
		// весь размер в байтах делим на сумму всех названий разделов
		// и на названия ячеек, все это умножено на 2, т.к. под каждый char
		// выделено 2 байта(16 бит)
		// получим длину слова в количестве по два байта
		int wordLenth = size / ((nmas * ndat + nmas) * 2);
		// создаем слово с 0 по wordLenth-1
		String word = filledString.substring(0, wordLenth - 1);
		// цикл по i - сколько разделов
		// цикл по j - сколько ячеек в разделе(слов)
		// System.out.println("" + word);
		for (int j = 0; j < ndat; j++) {
			list.add(word); // добавляем слово
			// System.out.println("такс такс");
		}
		for (int i = 0; i < nmas; i++) {
			
			/*for(int k = 0; k < list.size();++k)
			{
				System.out.println(list.get(k));
			}*/
			obj.put(word, list); // кладем в объект
			//list.clear(); // чистим лист
		}
		
		s = obj.toString(); // преобразуем в string
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
							// бесконечный цикл
							while (true) {
								// если есть входящее сообщение
								if (socket.getInputStream().available() != 0) {
									// считываем его
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