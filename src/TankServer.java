import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TankServer {

	public static final int TCP_PORT = 18104;
	private static int ID = 100;
	List<Client> clients = new ArrayList<>();

	public static void main(String args[]) {
		new TankServer().launch();
	}
	
	ServerSocket ss = null;
	Socket s = null;
	
	public void launch() {
		try {
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			while(true) {
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				int udpPort = dis.readInt(); //get udpPort from TankClient
				String IP = s.getInetAddress().getHostAddress(); //Client IP address
				clients.add(new Client(IP, udpPort));
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++);
System.out.print("A Client connected! ");
System.out.println("Addr:" + s.getInetAddress() + " port:" + s.getPort() + "udpPort: " + udpPort); //remote port
				s.close(); //TCP only use once; then close;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(s != null) s.close();
				if(ss != null) ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class Client {
		
		String IP;
		int udpPort;
		
		public Client(String IP, int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}
		
	}
	
}
