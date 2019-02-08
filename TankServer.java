import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tank Server 端
 * @author TinyA
 * @date 2018/10/7
 */
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
	
//	JTextArea jta = null;
	
	/*
	 * Server don't need GUI, but run jar cannot Close directly
	 * so, build an Window in order to close the Server.jar
	 */
	/*
	private void buildFrame() { 
		
		jta = new JTextArea("Server Start\n");
		
		this.setTitle("TankServer");
		this.setSize(400, 300);
		this.setLocation(50, 50);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		this.add(new JScrollPane(jta));
//		this.setVisible(true);
		this.setVisible(false);
		
	}
	*/
	/*
	private void textAreaAppend(String str) {
		jta.append(str);
		jta.selectAll();
		jta.setCaretPosition(jta.getSelectedText().length() - 1);
	}
	*/
	/*
	private void showErrorMsgDialog(String str) {
		JOptionPane.showMessageDialog(this, str);
	}
	*/
	public void launch() {
//		buildFrame();
		
		new Thread(new UDPClient()).start();
		
		try { //divide from Socket
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e1) {
//			showErrorMsgDialog("Address already in use 1\n which means Server has already existed!");
			System.exit(0);
		}
		
		/**
		 * 接收 Clients，读取 udpPort，发送 ID 给 Client
		 */
		try {
			while(true) {
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress(); //Client IP Address
				int udpPort = dis.readInt(); //Client's UDP port

				Client c = new Client(IP, s.getPort(), udpPort, dis);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++); 
				
				new Thread(c).start();
				/*
				textAreaAppend("A Client connected! ");
				textAreaAppend("-Addr:" + s.getInetAddress() + " -tcpPort:" + s.getPort() + 
							" -udpPort: " + udpPort + "\n");
				*/
//				s.close(); //Server's Socket need accept the Message of clientQuit，
						//so need connect all the time
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
	
	/**
	 * Server 端储存 Clients 
	 */
	private class Client implements Runnable {
		
		private static final int QUIT = 1;
		String IP;
		int tcpPort;
		int udpPort;
		private DataInputStream dis;
		
		public Client(String IP, int tcpPort, int udpPort) {
			this.IP = IP;
			this.tcpPort = tcpPort;
			this.udpPort = udpPort;
		}

		public Client(String IP, int tcpPort, int udpPort, DataInputStream dis) {
			this(IP, tcpPort, udpPort);
			this.dis = dis;
		}

		/**
		 * 监听 Client Quit，并移除该 Client
		 */
		@Override
		public void run() {
			try {
				if(dis.readInt() == QUIT) { //readInt block method || normal quit
					clients.remove(this);
//					textAreaAppend("A Client quited! ");
//					textAreaAppend("-Addr:" + IP + " -tcpPort:" + tcpPort + " -udpPort:" + udpPort + "\n");
				}
			} catch (IOException e) {
				if(clients.contains(this)) { //same UDP port & Address Error quit
					clients.remove(this);
//					textAreaAppend("A Client quited! ");
//					textAreaAppend("-Addr:" + IP + " -tcpPort:" + tcpPort + " -udpPort:" + udpPort + " repeat\n");
					ID--;
					/*
					 * same color BUG after enter same Client UDP port & address
					 * ID fall back
					 * same logic: when new 10 enmeyTanks, but collide with other when new Tank
					 * so i--; loop renew another Tank, drop/discard the collide one.
					 */
				} 
			}
		}
		
	}
	
	/**
	 * 起线程不断接收 Client UDP 数据
	 */
	private class UDPClient implements Runnable { //Like Chat's Client

		DatagramSocket ds = null;

		@Override
		public void run() {
			
			try {
				ds = new DatagramSocket(UDP_PORT);  //Server's UDP port, not Client's
			} catch (SocketException e1) {
//				showErrorMsgDialog("Address already in use 2\n which means Server has already existed!");
				System.exit(0);
			}
			
			byte buf[] = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			
			/**
			 * 接收包并发还给每个 Client
			 */
			while(ds != null) {
				try {
					ds.receive(dp); //DatagramSocket receive message from Server's UDP_PORT
									//dp will be initialized when Client send packet
									//receive() is a block method, run next step until receive something
									//So this Thread can start() at anywhere, whether before or after TCP
					for(int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort)); 
						//send to Client's Socket, need point to Client's exactly Address, 
						//cause UDP need point to where the message should be send
						ds.send(dp);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
