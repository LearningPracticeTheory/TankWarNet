import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	}

	private void send(TankNewMsg msg) {
		msg.send(ds, "localhost", TankServer.UDP_PORT); //message send to Server
			//use Server's ID & UDP_PORT 
	}
	
}
