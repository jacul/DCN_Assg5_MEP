import java.util.HashMap;

/**
 * Xiangdong Zhu CSC 623 - Data communication and network
 * 
 * @author zxd
 * 
 */
public class MEPServer extends Thread {
	/**
	 * Containing all client information, ip address and port number.<br>
	 * The key is the name of the client, and the value should be in this
	 * format, IP:PORT, for example, localhost:20010.
	 */
	static final HashMap<String, String>	DNSTable	= new HashMap<String, String>();

	static {
		DNSTable.put("doc", "localhost:20010");
		DNSTable.put("grumpy", "localhost:20011");
		DNSTable.put("sleepy", "localhost:20012");
		DNSTable.put("sneezy", "localhost:20013");
		DNSTable.put("dopey", "localhost:20014");
		DNSTable.put("bashful", "localhost:20015");
		DNSTable.put("happy", "localhost:20016");
	}
	
	private int								port;

	/**
	 * Constructor, with the port number.
	 * 
	 * @param port
	 */
	public MEPServer(int port) {
		this.port = port;
	}

	public void run() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int defaultport = 20000;
		if (args != null && args.length > 0) {
			try {
				defaultport = Integer.parseInt(args[0]);
			} catch (Exception e) {}
		}
		MEPServer server = new MEPServer(defaultport);
		server.start();
	}

}
