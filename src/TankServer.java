import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TankServer extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final int TCP_PORT = 18104;
	public static final int UDP_PORT = 8888;
	private static int ID = 100;
	List<Client> clients = new ArrayList<>();

	public static void main(String args[]) {
		new TankServer().launch();
	}
	
	ServerSocket ss = null;
	Socket s = null;
	
	/*
	 * Server don't need GUI, but run jar cannot Close directly
	 * so, build an Window in order to close the Server
	 */
	JTextArea jta = null;
	
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
		this.setVisible(true);
		
	}
	
	private void textAreaAppend(String str) {
		jta.append(str);
		jta.selectAll();
		jta.setCaretPosition(jta.getSelectedText().length() - 1);
	}
	
	private void showErrorMsgDialog(String str) {
		JOptionPane.showMessageDialog(this, str);
	}
	
	public void launch() {
		buildFrame();
		
		new Thread(new UDPClient()).start();
		
		try { //divide from Socket
			ss = new ServerSocket(TCP_PORT);
//System.out.println("ServerSocket: " + ss.getLocalSocketAddress()); //== getInetAddress + getLocalPort
		//0.0.0.0/0.0.0.0:18104 TCP_PORT
		} catch (IOException e1) {
//			e1.printStackTrace();
			showErrorMsgDialog("Address already in use 1\n which means Server has already existed!");
			System.exit(0);
			
		}
		
		try {
			while(true) {
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress(); //Client IP Address
				int udpPort = dis.readInt(); //Client's UDP port
/*
System.out.println("Cient IP-" + IP);
System.out.println(s.getInetAddress());
System.out.println(s.getLocalAddress());
System.out.println(s.getLocalSocketAddress());
*/				
				Client c = new Client(IP, s.getPort(), udpPort, dis);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++); 
				
				new Thread(c).start();
				
				textAreaAppend("A Client connected! ");
				textAreaAppend("-Addr:" + s.getInetAddress() + " -tcpPort:" + s.getPort() + 
							" -udpPort: " + udpPort + "\n");		
							//remote port which is Client's UDP port

//				s.close(); //TCP only use once; then close;
				//Server's Socket need accept the Message of clientQuit，so need connect all the time
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

		@Override
		public void run() {
			try {
				if(dis.readInt() == QUIT) { //readInt block method || normal quit
					clients.remove(this);
//System.out.println("Client size:" + clients.size());
					
					textAreaAppend("A Client quited! ");
					textAreaAppend("-Addr:" + IP + " -tcpPort:" + tcpPort + " -udpPort:" + udpPort + "\n");
				}
			} catch (IOException e) {
//				e.printStackTrace();
//				showErrorMsgDialog("Connection Reset, which means Client has Same Address & UDP port");
				if(clients.contains(this)) { //same UDP port & Address Error quit
					clients.remove(this);
					textAreaAppend("A Client quited! ");
					textAreaAppend("-Addr:" + IP + " -tcpPort:" + tcpPort + " -udpPort:" + udpPort + " repeat\n");
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
	
	private class UDPClient implements Runnable { //Like Chat's Client

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
/*				
System.out.println("DS " + ds.getInetAddress()); //Null
System.out.println("DS " + ds.getLocalAddress()); //0.0.0.0/0.0.0.0
System.out.println("DS " + ds.getRemoteSocketAddress()); //Null
System.out.println("DS " + ds.getLocalSocketAddress()); //0.0.0.0/0.0.0.0:8888 Server UDP port
*/
			} catch (SocketException e1) {
//				e1.printStackTrace();
				showErrorMsgDialog("Address already in use 2\n which means Server has already existed!");
				System.exit(0);
			}
			
//System.out.println("UDP Socket started at udpPort: " + UDP_PORT);
			
			byte buf[] = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			while(ds != null) {
				try {
					ds.receive(dp); //DatagramSocket receive message from Server's UDP_PORT
									//Not Client's UDP_PORT
									//dp will be initialized when Client send packet
									//receive() is a block method, run next step until receive something
									//So this Thread can start() at anywhere, whether before or after TCP
//System.out.println("a packet received from Client " + dp.getSocketAddress());
					for(int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort)); 
						//send to Client's Socket, need point to Client's exactly Address, 
						//cause UDP need point to where the message should be send
						//BUT NOT dp.setAddress(address); this method set Server's address (itself)
						ds.send(dp);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
