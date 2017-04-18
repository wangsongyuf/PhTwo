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

	@SuppressWarnings("resource")
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

			while (true) {
				int count = 0;
				count = loopleft(leftOutputStr, count);
				while (count != 0) {
					removeleft();
					count--;
				}
				count = 0;
				count = loopright(rightOutputStr, count);
				while (count != 0) {
					removeright();
					count--;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private synchronized int loopright(DataOutputStream rightOutputStr, int count) throws IOException {
		for (String s : rightwrite) {
			count++;

			rightOutputStr.writeBytes(s);
		}
		return count;
	}

	private synchronized int loopleft(DataOutputStream leftOutputStr, int count) throws IOException {
		for (String s : leftwrite) {
			count++;

			leftOutputStr.writeBytes(s);

		}
		return count;
	}

	public synchronized String removeleft() {
		return leftwrite.pop();
	}

	public synchronized String removeright() {
		return rightwrite.pop();
	}

	public synchronized void addleft(String s) {
		leftwrite.add(s);

	}

	public synchronized void addright(String s) {
		rightwrite.add(s);

	}

}
