import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankNewMsg implements Msg {

	int msgType = Msg.TANK_NEW_MSG;
	Tank t = null;
	TankClient tc = null;
	
	TankNewMsg(Tank t) {
		this.t = t;
	}

	public TankNewMsg(TankClient tc) {
		this.tc = tc;
	}

	//Server's IP & UDP_PORT
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(t.ID);
			dos.writeInt(t.x);
			dos.writeInt(t.y);
			dos.writeInt(t.dir.ordinal()); //GunBarrel's Direction decide Tank's direction
										 //but use tank.dir to identify
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

	public void parse(DataInputStream dis) {
		try {
			int ID = dis.readInt();
			if(tc.myTank.ID == ID) {
				return;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()]; //find index and getValuse of index
			boolean good = dis.readBoolean();
//System.out.println("ID:" + ID + " x:" + x + " y:" + y + " dir:" + dir + " good:" + good);
			Tank t = new Tank(x, y, good, tc);
			t.dir = dir;
			t.ID = ID;
			tc.tanks.add(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
}
