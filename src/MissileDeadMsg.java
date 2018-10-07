import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg {

	int msgType = Msg.MISSILE_DEAD_MSG;
	int tankID;
	int ID;
	TankClient tc = null;
	
	public MissileDeadMsg(int tankID, int ID) { //send
		this.tankID = tankID;
		this.ID = ID;
	}
	
	public MissileDeadMsg(TankClient tc) { //receive
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankID);
			dos.writeInt(ID);
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
			int ID = dis.readInt();
			for(int i = 0; i < tc.missiles.size(); i++) {
				Missile m = tc.missiles.get(i);
				if(m.tankID == tankID && m.ID == ID) {
//					tc.tanks.remove(t);
					m.setLive(false); //each TankClient will KIA this Tank after next refresh
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
