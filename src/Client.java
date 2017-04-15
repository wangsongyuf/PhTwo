import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

public class Client implements Runnable {
	public ArrayDeque<String> leftwrite = new ArrayDeque<>();
	public ArrayDeque<String> rightwrite = new ArrayDeque<>();
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
			// clients setup
//			Socket leftSock = null;
//			DataOutputStream leftOutputStr = null;
//			BufferedReader leftInputStr = null;
//			Socket rightSock = null;
//			DataOutputStream rightOutputStr = null;
//			BufferedReader rightInputStr = null;
//			leftSock = new Socket(leftaddress, port);
//			leftOutputStr = new DataOutputStream(leftSock.getOutputStream());
//			leftInputStr = new BufferedReader(new InputStreamReader(leftSock.getInputStream()));
//			rightSock = new Socket(rightaddress, port);
//			rightOutputStr = new DataOutputStream(rightSock.getOutputStream());
//			rightInputStr = new BufferedReader(new InputStreamReader(rightSock.getInputStream()));
			System.out.println("try to connect server");
			SocketChannel left = SocketChannel.open();
			left.configureBlocking(true);
			left.connect(new InetSocketAddress(leftaddress, port));
			left.configureBlocking(false);
			SocketChannel right = SocketChannel.open();
			right.configureBlocking(true);
			right.connect(new InetSocketAddress(rightaddress, port));
			right.configureBlocking(false);
			
			System.out.println("start write");
			while(true){
				if(leftwrite.size()>0||rightwrite.size()>0){
				System.out.println("---------------------------------------------------");
				for(String s:leftwrite){
					System.out.println("leftToWrite: "+s);
				}
				for(String s:rightwrite){
					System.out.println("rightToWrite: "+s);
				}
				System.out.println("---------------------------------------------------");
				}
			int count=0;
			for (String s : leftwrite) {
				count++;
				
				ByteBuffer buf = ByteBuffer.allocate(1024);
				buf.clear();
				buf.put(s.getBytes());

				buf.flip();
				left.write(buf);
				
			}
			while(count!=0){
				leftwrite.pop();
				count--;
			}
			 count=0;
			for (String s : rightwrite) {
				count++;
				ByteBuffer buf = ByteBuffer.allocate(1024);
				buf.clear();
				buf.put(s.getBytes());

				buf.flip();
				right.write(buf);
			}
			while(count!=0){
				rightwrite.pop();
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
