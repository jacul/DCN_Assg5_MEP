
/**
 * 
 * @author zxd
 * 
 */
public class Client extends Thread {

	public Client(String name) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			new Client(args[0]).start();
		} else {
			System.out.println("Specify a client name!");
			System.exit(0);
		}
	}

}
