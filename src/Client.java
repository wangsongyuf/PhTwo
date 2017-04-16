import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {
	public LinkedBlockingQueue<String> leftwrite = new LinkedBlockingQueue<>();
	public LinkedBlockingQueue<String> rightwrite = new LinkedBlockingQueue<>();
	String leftaddress, rightaddress;
	int port;

	public Client(String leftaddress, String rightaddress, int port) {
		this.leftaddress = leftaddress;
		this.rightaddress = rightaddress;
		this.port = port;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(10000L);

			Socket leftSock = null;
	DataOutputStream leftOutputStr = null;
			BufferedReader leftInputStr = null;
				Socket rightSock = null;
		DataOutputStream rightOutputStr = null;
			BufferedReader rightInputStr = null;
			leftSock = new Socket(leftaddress, port);
				leftOutputStr = new DataOutputStream(leftSock.getOutputStream());
			leftInputStr = new BufferedReader(new InputStreamReader(leftSock.getInputStream()));
			rightSock = new Socket(rightaddress, port);
			rightOutputStr = new DataOutputStream(rightSock.getOutputStream());
			rightInputStr = new BufferedReader(new InputStreamReader(rightSock.getInputStream()));
			System.out.println("start write");
			while(true){
//				if(leftwrite.size()>0||rightwrite.size()>0){
//				System.out.println("---------------------------------------------------");
//				for(String s:leftwrite){
//					System.out.println("leftToWrite: "+s);
//				}
//				for(String s:rightwrite){
//					System.out.println("rightToWrite: "+s);
//				}
//				System.out.println("---------------------------------------------------");
//				}
			int count=0;
			for (String s : leftwrite) {
				count++;
				

				leftOutputStr.writeBytes(s);
				
			}
			while(count!=0){
				leftwrite.take();
				count--;
			}
			 count=0;
			for (String s : rightwrite) {
				count++;

				rightOutputStr.writeBytes(s);
			}
			while(count!=0){
				rightwrite.take();
				count--;
			}}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
