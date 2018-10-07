import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetClient {
		
	private static final int UDP_PORT = 6666; //different with Server's
										//change every single time when in one machine
										//or will throw Address already used
	int udpPort;
	TankClient tc;

	NetClient(TankClient tc) {
		udpPort = UDP_PORT; //Client's UDP_PORT for receive message
		this.tc = tc; //but send to Server use server's UDP
	}
	
	DatagramSocket ds = null;
	
	public void connect(String IP, int tcpPort) {
		Socket s = null;
		try {
			s = new Socket(IP, tcpPort); //TCP connect
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort); //UDP send & receive message --> write udpPort to Server and initialized one of client
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int ID = dis.readInt();
			tc.myTank.ID = ID;
			ds = new DatagramSocket(udpPort);
			TankNewMsg msg = new TankNewMsg(tc.myTank);
			send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(s != null) {
					s.close();
					s = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		new Thread(new UDPRecvThread()).start();
		
	}

	public void send(Msg msg) {
		msg.send(ds, "localhost", TankServer.UDP_PORT); //message send to Server
			//use Server's ID & UDP_PORT
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
//System.out.println("TankMoveMsg parse"); //Debug
				break;
			}
		}
		
	}
	
}
