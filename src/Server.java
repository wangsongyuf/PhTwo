import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

class Server implements Runnable {
	public ArrayDeque<String> leftread = new ArrayDeque<>();
	public ArrayDeque<String> rightread = new ArrayDeque<>();

	String leftaddress, rightaddress;
	int port;

	public Server(String leftaddress, String rightaddress, int port) {
		this.leftaddress = leftaddress;
		this.rightaddress = rightaddress;
		this.port = port;
	}

	public synchronized void addleft(String s) {
		leftread.add(s);
	}

	public synchronized void addright(String s) {
		rightread.add(s);

	}

	public synchronized String removeleft() {
		return leftread.pop();
	}

	public synchronized String removeright() {
		return rightread.pop();
	}

	@Override
	public void run() {

		try {
			Socket leftserver = null;
			Socket rightserver = null;
			ServerSocket servSock = null;
			servSock = new ServerSocket(port);
			Socket clientSockone = servSock.accept();
			if (clientSockone.getInetAddress().toString().contains(leftaddress)) {
				leftserver = clientSockone;
			} else {
				rightserver = clientSockone;
			}

			Socket clientSocktwo = servSock.accept();

			if (clientSocktwo.getInetAddress().toString().contains(rightaddress)) {
				rightserver = clientSocktwo;
			} else {
				leftserver = clientSocktwo;
			}
			BufferedReader leftinputStr;
			DataOutputStream leftoutputStr;
			String rightinputLine;
			BufferedReader rightinputStr;
			DataOutputStream rightoutputStr;
			leftinputStr = new BufferedReader(new InputStreamReader(leftserver.getInputStream()));
			leftoutputStr = new DataOutputStream(leftserver.getOutputStream());
			rightinputStr = new BufferedReader(new InputStreamReader(rightserver.getInputStream()));
			rightoutputStr = new DataOutputStream(rightserver.getOutputStream());

			Thread t1 = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						String temp;
						try {
							temp = leftinputStr.readLine();
							addleft(temp);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			});
			Thread t2 = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						String temp;
						try {
							temp = rightinputStr.readLine();
							addright(temp);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			});
			t1.start();
			t2.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

};