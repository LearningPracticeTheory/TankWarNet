import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg {

	int ID;
	int msgType = Msg.TANK_MOVE_MSG;
	Direction dir = null;
	TankClient tc;
	private int x;
	private int y;

	public TankMoveMsg(int ID, int x, int y, Direction dir) {
		this.ID = ID;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}
	
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msgType);
			dos.writeInt(ID);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte buf[] = baos.toByteArray(); //getByteArray and packet it
		DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));

		try {
			ds.send(dp); //send packet to Server, which need point out Server's IP & UDP CORRECT
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void parse(DataInputStream dis) {
		try {
			int ID = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			
			for(int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.ID == ID) {
					t.x = x;
					t.y = y;
					t.dir = dir;
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
