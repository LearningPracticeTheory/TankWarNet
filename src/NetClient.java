import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetClient {
		
//	private static final int UDP_PORT = 6665; //different with Server's
										//change every single time when in one machine
										//or will throw Address already used
//	int udpPort;
	TankClient tc;
	String serverIP = null;

	NetClient(TankClient tc) {
//		udpPort = UDP_PORT; //Client's UDP_PORT for receive message
		this.tc = tc; //but send to Server use server's UDP
	}
	
	DatagramSocket ds = null;
	DataOutputStream dos = null;
	
	Socket s = null;
	
	public void connect(String serverIP, int tcpPort, int udpPort) { //Server's IP & TCP_PORT + Client's UDP_PORT
		try {
			this.serverIP = serverIP;
//			s = new Socket(IP, tcpPort); //TCP connect Client's IP & Server's TCP_PORT ERROR
			s = new Socket(serverIP, tcpPort);
			
			/*
			 * Server's IP & TCP_PORT CORRECT
			 * Server will get the source of Socket where it from
			 * example: In Server, s.getInetAddress(); will get the Client's Address
			 * so Client Don't need send the IP to Server, 
			 * which means Server.accept().getInetAddress(); will get the IP of Client
			 */
/*			
System.out.println(s.getInetAddress()); // /127.0.0.1
System.out.println(s.getLocalAddress()); // /127.0.0.1
System.out.println(s.getRemoteSocketAddress()); // /127.0.0.1:18104 remote
System.out.println(s.getLocalSocketAddress()); // /127.0.0.1:13176 local
*/
			dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort); //UDP send & receive message --> write udpPort to Server and initialized one of client
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int ID = dis.readInt();
			tc.myTank.ID = ID;
			
			if(ID % 2 == 0) {
				tc.myTank.setGood(true);
			} else {
				tc.myTank.setGood(false);
			}
			
			ds = new DatagramSocket(udpPort); //No binding in to Socket, just get Client's UDP port
			//but DatagrameSocket.send(new DatagramPacket(byte[], length, InetSocketAddress(IP, UDP_port)));
			//InetSocketAddress's argument IP will point to the Address & UDP port
			//example: Server's IP & UPD_Port
			TankNewMsg msg = new TankNewMsg(tc.myTank);
			send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*try {
				if(s != null) {
					s.close();
					s = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		
		new Thread(new UDPRecvThread()).start();
		
	}

	public void send(Msg msg) {
//		msg.send(ds, "localhost", TankServer.UDP_PORT); //message send to Server
			//use Server's ID & UDP_PORT
		msg.send(ds, serverIP, TankServer.UDP_PORT); //send to Server by Server IP & UDP_PORT
	}
	
	private class UDPRecvThread implements Runnable {  //like Chat's ServerInfoThread

//		DatagramSocket ds = null; //DatagramSocket already exist
		
		byte buf[] = new byte[1024];
		
		@Override
		public void run() {
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			while(true) {
				try {
					ds.receive(dp); //Received packet from Server
System.out.println("A packet received from Server");
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void parse(DatagramPacket dp) { //like send
			ByteArrayInputStream dais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(dais);
//			TankNewMsg msg = new TankNewMsg(tc.myTank); //the message belong different Tank, not always myTank
			try {
				int msgType = dis.readInt(); //Not read in Msg, NetClient manage msgType, which is a smart way
//System.out.println("MsgType:" + msgType); //receive 0 -> ERROE -> MoveMsg.msgType need initialized
				msgManage(msgType, dis);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		private void msgManage(int msgType, DataInputStream dis) {
			Msg msg = null;
			switch(msgType) {
			case Msg.TANK_NEW_MSG:
				msg = new TankNewMsg(tc);
				msg.parse(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				msg = new TankMoveMsg(tc);
				msg.parse(dis);
//System.out.println("TankMoveMsg parse");
				break;
			case Msg.MISSILE_NEW_MSG:
				msg = new MissileNewMsg(tc);
				msg.parse(dis);
				break;
			case Msg.TANK_DEAD_MSG:
				msg = new TankDeadMsg(tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_DEAD_MSG:
				msg = new MissileDeadMsg(tc);
				msg.parse(dis);
				break;
			case Msg.TANK_REBORN_MSG:
				msg = new TankRebornMsg(tc);
				msg.parse(dis);
				break;
			}
		}
		
	}

	
}
