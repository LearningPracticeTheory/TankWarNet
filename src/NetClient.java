import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetClient {
		
	String host;
	int port;
	
	NetClient(String host, int port) {
		try {
			Socket s = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
