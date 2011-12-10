/**
 * Xiangdong Zhu CSC 623 - Data communication and network
 *  12/10/2011
 * Fall 2011 Extra Credit Assignment #5 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the server. It can run on local server, listening for incoming
 * connections. Its default port number is 20000, you can specify a port number
 * just by appending a argument when starting this server in java command line.
 * 
 * @author zxd
 * 
 */
public class MEPServer extends Thread {
	/**
	 * Containing all clients that are online. Every client has a thread running
	 * that is listening for its incoming messages.
	 */
	private HashMap<String, Socket> availableClient = new HashMap<String, Socket>();
	/**
	 * Containing all clients that are not handled.
	 */
	private static ArrayList<Socket> unhandledRequest = new ArrayList<Socket>();
	/**
	 * Port number at which the server is listening.
	 */
	private int port;

	/**
	 * Constructor, with the port number.
	 * 
	 * @param port
	 */
	public MEPServer(int port) {
		this.port = port;
	}

	/**
	 * Start the listener of this server. Also, it accepts every incoming
	 * connection and processes it.
	 */
	public void startListening() {
		System.out.println("Server started");
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			while (true) {
				Socket client = server.accept();
				System.out.println("Client connected");
				unhandledRequest.add(client);
				new Thread(this).start();
			}
		} catch (IOException e) {
			System.err.println("4: " + e.getClass().getName() + e.getMessage());
		} finally {
			if (server != null)
				try {
					server.close();
				} catch (IOException e) {
					System.err.println("3: " + e.getClass().getName() + e.getMessage());
				}
		}

	}

	public void run() {
		Socket client = unhandledRequest.remove(0);
		if (client == null)
			return;
		BufferedReader reader = null;
		PrintWriter sender = null;
		String clientname = null;

		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			sender = new PrintWriter(client.getOutputStream());
			clientname = welcomeClient(client, reader, sender);
			if (clientname == null) {
				return;
			}
		} catch (IOException e) {
			return;
		}
		while (true) {
			try {
				String msg = reader.readLine();
				if (msg == null) {
					// the client may have closed the connection, thread quits.
					client.close();
					return;
				}
				handleMessage(clientname, msg, reader, sender);
			} catch (SocketException e) {
				try {
					client.close();
					return;
				} catch (IOException e1) {
				}
			} catch (IOException e) {
				System.out.println("Reading message error: " + e.getClass().getName() + e.getMessage());
			}
		}

	}

	/**
	 * Send a welcome message to the client.
	 * 
	 * @param client
	 * @param reader
	 * @param sender
	 * @return
	 * @throws IOException
	 */
	private String welcomeClient(Socket client, BufferedReader reader, PrintWriter sender) throws IOException {
		String clientname = null;
		String status = reader.readLine();
		System.out.println("Incoming: " + status);

		if (status.toLowerCase().startsWith("client ") && status.toLowerCase().endsWith(" online")) {
			clientname = status.substring(status.indexOf(' ') + 1, status.lastIndexOf(' '));
			availableClient.put(clientname, client);
			sender.println("#Welcome! " + clientname);
			sender.flush();
		} else {
			// Not a valid login message, close the connection.
			client.close();
		}
		return clientname;
	}

	/**
	 * Handles the message.
	 * 
	 * @param clientname
	 * @param msg
	 * @param reader
	 * @param sender
	 * @throws IOException
	 */
	private void handleMessage(String clientname, String msg, BufferedReader reader, PrintWriter sender) throws IOException {
		if (msg.equalsIgnoreCase("logoff")) {
			// client exits
			availableClient.remove(clientname);
		} else if (msg.startsWith("send ")) {
			String dstclient = msg.substring(5);
			if (!availableClient.containsKey(dstclient)) {
				sender.println("#Client " + dstclient + " Not Available!");
				sender.flush();
				return;
			}
			String text = reader.readLine();
			if (text != null) {
				Socket dstclientsocket = availableClient.get(dstclient);
				if (dstclientsocket.isClosed()) {
					availableClient.remove(dstclient);
					sender.println("#Client " + dstclient + " Not Available!");
					sender.flush();
					return;
				}
				PrintWriter msgsender = new PrintWriter(dstclientsocket.getOutputStream());
				msgsender.println(clientname + ">" + text);
				msgsender.flush();
			}
		} else {
			sender.println("#Unrecognized message!");
			sender.flush();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int defaultport = 20000;
		if (args != null && args.length > 0) {
			// User specifies a port number.
			try {
				defaultport = Integer.parseInt(args[0]);
			} catch (Exception e) {
			}
		}
		new MEPServer(defaultport).startListening();
	}

}
