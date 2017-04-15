import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
			for (String s : leftwrite) {
				leftOutputStr.writeBytes(s);
			}
			for (String s : rightwrite) {
				rightOutputStr.writeBytes(s);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
