import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankRebornMsg implements Msg {

	int msgType = Msg.TANK_REBORN_MSG;
	private TankClient tc;
	private Tank rebornTank;
	
	public TankRebornMsg(Tank rebornTank) {
		this.rebornTank = rebornTank;
	}
	
	public TankRebornMsg(TankClient tc) {
		this.tc = tc;
	}
	
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			
System.out.println("rebornTank is NULL send ?" + (rebornTank == null));
			dos.writeInt(msgType);
			dos.writeInt(rebornTank.ID);
			dos.writeInt(rebornTank.x);
			dos.writeInt(rebornTank.y);
			dos.writeInt(rebornTank.dir.ordinal());
			dos.writeInt(rebornTank.gb.dir.ordinal());
			dos.writeBoolean(rebornTank.isGood());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte buf[] = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
		try {
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		try {
			int tankID = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			Direction tankDir = Direction.values()[dis.readInt()];
			Direction gbDir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			/*
			 * confirm this Tank which with this tankID is not exist receive 
			 */ 
System.out.println("rebornTank is NULL receive?" + (rebornTank == null));
			if(tankID == tc.myTank.ID) {
				return;
			} else if(rebornTank == null) {
				rebornTank = new Tank(x, y, good, tc);
				rebornTank.ID = tankID;
				rebornTank.dir = tankDir;
				rebornTank.gb.dir = gbDir;
				tc.tanks.add(rebornTank);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
