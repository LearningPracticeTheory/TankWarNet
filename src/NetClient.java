import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetClient {
		
	private static final int UDP_PORT = 8888;
	int udpPort;

	NetClient() {
		udpPort = UDP_PORT;
	}
	
	public void connect(String IP, int port) {
		try {
			Socket s = new Socket(IP, port); //TCP connect
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.write(udpPort); //UDP send & receive message --> write udpPort to Server and initialized one of client
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
