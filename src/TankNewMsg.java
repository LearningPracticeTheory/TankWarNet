import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankNewMsg {

	Tank t = null;
	
	TankNewMsg(Tank t) {
		this.t = t;
	}

	//Server's IP & UDP_PORT
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(t.ID);
			dos.writeInt(t.x);
			dos.writeInt(t.y);
			dos.writeInt(t.gb.dir.ordinal()); //GunBarrel's Direction decide Tank's direction
			dos.writeBoolean(t.isGood());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte buf[] = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
		try {
			ds.send(dp); //DatagramSocket.send(DatagramPacket); -> socket send a packet to server
						//this.dp will initialize the Server's receive dp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
}
