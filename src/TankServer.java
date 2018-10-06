import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {

	public static final int TCP_PORT = 18104;
	public static final int UDP_PORT = 8888;
	private static int ID = 100;
	List<Client> clients = new ArrayList<>();

	public static void main(String args[]) {
		new TankServer().launch();
	}
	
	ServerSocket ss = null;
	Socket s = null;
	
	public void launch() {
		new Thread(new UDPClient()).start();
		
		try { //divide from Socket
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
	
	private class UDPClient implements Runnable {

		DatagramSocket ds = null;
		/*		
		UDPClient(int udpPort) { //DatagrameSocket receive message for Server
			this.udpPort = udpPort;
			try {
				ds = new DatagramSocket(udpPort);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		*/
		@Override
		public void run() {
			
			try {
				ds = new DatagramSocket(UDP_PORT);  //Server's UDP port, not Client's
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			
System.out.println("UDP Socket started at udpPort: " + UDP_PORT);
			
			byte buf[] = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			while(ds != null) {
				try {
					ds.receive(dp); //DatagramSocket receive message from Server's UDP_PORT
									//Not Client's UDP_PORT
									//dp will be initialized when Client send packet
									//receive() is a block method, run next step until receive something
									//So this Thread can start() at anywhere, whether before or after TCP
System.out.println("a packet received from Client");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
