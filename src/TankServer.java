import java.io.IOException;
import java.net.ServerSocket;

public class TankServer {

	public static final int TCP_PORT = 18104;

	public static void main(String args[]) {
		new TankServer().launch();
	}
	
	public void launch() {
		try {
			ServerSocket ss = new ServerSocket(TCP_PORT);
			ss.accept();
//System.out.println("A Client connected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
