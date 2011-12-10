/**
 * Xiangdong Zhu CSC 623 - Data communication and network
 * 12/10/2011
 * Fall 2011 Extra Credit Assignment #5 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Client to send messages. It connects to the server and exchanges messages.<br>
 * When use it, you should either specify a client name or a client name with
 * server address and port number.
 * 
 * @author zxd
 * 
 */
public class Client extends Thread {
	/**
	 * Socket connection this client uses.
	 */
	private Socket socket = null;
	/**
	 * Name of this client.
	 */
	private String clientname = null;
	/**
	 * Server's address. By default it's local.
	 */
	private String serveraddress = "localhost";
	/**
	 * Port number of the server. By default it's 20000.
	 */
	private int serverport = 20000;
	/**
	 * To send message.
	 */
	private PrintWriter msgSender = null;
	/**
	 * Receive message.
	 */
	private BufferedReader msgReceiver = null;

	/**
	 * Constructor with client name only.
	 * 
	 * @param name
	 */
	public Client(String name) {
		this.clientname = name;
	}

	/**
	 * Constructor with client name and server url.
	 * 
	 * @param name
	 * @param server
	 */
	public Client(String name, String server, int serverport) {
		this.clientname = name;
		this.serveraddress = server;
		this.serverport = serverport;
	}

	public void run() {
		connectToServer();
		prepareMessageExchange();
		Scanner userinput = new Scanner(System.in);
		while (true) {
			// read input from keyboard
			String input = userinput.nextLine();
			// send to server
			msgSender.println(input);
			msgSender.flush();
			if (input.equalsIgnoreCase("logoff")) {
				// user logs off, exit the program.
				if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
						System.out.println(e.getClass().getName() + e.getMessage());
					}
				return;
			} else if (input.toLowerCase().startsWith("send ")) {
				System.out.print(">");
			}
		}
	}

	/**
	 * Start a Thread to read responses and send an online notification to
	 * server.
	 */
	private void prepareMessageExchange() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						String msg = msgReceiver.readLine();
						if (msg == null) {
							// server not available
							System.out.println("Server not reachable! Quit.");
							System.exit(0);
						}
						System.out.println(msg);

					} catch (Exception e) {
						System.exit(0);
					}
				}
			}
		}.start();
		msgSender.println("Client " + clientname + " online");
		msgSender.flush();
	}

	private void connectToServer() {
		try {
			socket = new Socket(serveraddress, serverport);
			msgSender = new PrintWriter(socket.getOutputStream());
			msgReceiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			// Server not available
			System.out.println("Can't find server!");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length == 1) {
			// user provides server name only.
			new Client(args[0]).start();
		} else if (args != null && args.length == 3) {
			try {
				int port = Integer.parseInt(args[2]);
				new Client(args[0], args[1], port).start();
			} catch (Exception e) {
				System.out.println("Please specify a valid server port number!");
				System.exit(0);
			}
		} else {
			System.out.println("Specify a client name [server address and port number]!");
			System.exit(0);
		}
	}
}
