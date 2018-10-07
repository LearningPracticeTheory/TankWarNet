import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

/**
 * TankClient 所有 Net 相关的操作均由 NetClient 处理 
 */
public class NetClient {
		
//	private static final int UDP_PORT = 6665; //different with Server's
										//change every single time when in one machine
										//or will throw Address already used
	TankClient tc;
	String serverIP = null;

	NetClient(TankClient tc) {
		this.tc = tc;
	}
	
	DatagramSocket ds = null;
	DataOutputStream dos = null;
	
	Socket s = null;
	
	private void showErrorMsgDialog(String str) {
		JOptionPane.showMessageDialog(tc, str);
	}
	
	/**
	 * TCP 连接 Server
	 * @param serverIP server 端的 IP 地址
	 * @param tcpPort server 端的 TCP port
	 * @param udpPort 本地 udpPort
	 */
	public void connect(String serverIP, int tcpPort, int udpPort) { //Server's IP & TCP_PORT + Client's UDP_PORT
		try {
			this.serverIP = serverIP;
			s = new Socket(serverIP, tcpPort);
		} catch(UnknownHostException e) {
			showErrorMsgDialog("UnknownHostException, System exit");
			System.exit(0);
		} catch(NoRouteToHostException e) {
			showErrorMsgDialog("No route to host: connect\n System exit");
			System.exit(0);
		} catch(ConnectException e) { //ERROR
			showErrorMsgDialog("Connection refused: connect\n The IP of Server refused to connect\n "
					+ "Please enter another serverIP");
			tc.jtfServerIP.setText("127.0.0.1");
			tc.dialog = tc.new MyDialog(tc, "Enter Server's & Client's IP and UDP port", true);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Server will get the source of Socket where it from
		 * example: In Server, s.getInetAddress(); will get the Client's Address
		 * so Client Don't need send the IP to Server, 
		 * which means Server.accept().getInetAddress(); will get the IP of Client
		 */
		
		try {
			dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort); 
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int ID = dis.readInt();
			tc.myTank.ID = ID;
			
			if(ID % 2 == 0) {
				tc.myTank.setGood(true);
			} else {
				tc.myTank.setGood(false);
			}
			
			ds = new DatagramSocket(udpPort);
			TankNewMsg msg = new TankNewMsg(tc.myTank);
			send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(tc, "No Server or Address udpPort already in used \n System exit");
			try {
				dos.writeInt(TankClient.QUIT);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
		
		new Thread(new UDPRecvThread()).start();
		
	}

	/**
	 * 发送信息给 server
	 * @param msg 各种信息
	 */
	public void send(Msg msg) {
		msg.send(ds, serverIP, TankServer.UDP_PORT); //send to Server use Server IP & UDP_PORT
	}
	
	/**
	 * 起线程不断接收 Server 转发的 UDP 数据 
	 */
	private class UDPRecvThread implements Runnable {  //like Chat's ServerInfoThread

		byte buf[] = new byte[1024];
		
		@Override
		public void run() {
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			while(true) {
				try {
					ds.receive(dp); //Received packet from Server
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 解析数据
		 * @param dp 打包的数据
		 * @see java.net.DatagramPacket
		 */
		private void parse(DatagramPacket dp) { //like send
			ByteArrayInputStream dais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(dais);
			try {
				int msgType = dis.readInt(); //NetClient manage msgType, which is a smart way
				msgManage(msgType, dis);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		/**
		 * 信息管理，根据 msgType 解析不同的信息
		 * @param msgType 各种类型数据的标识
		 * @param dis 数据输入流
		 * @see java.io.DataInputStream
		 */
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
