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
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

class Server implements Runnable {
	public ArrayDeque<String> leftread = new ArrayDeque<>();
	public ArrayDeque<String> rightread = new ArrayDeque<>();
	public ArrayDeque<String> leftwrite = new ArrayDeque<>();
	public ArrayDeque<String> rightwrite = new ArrayDeque<>();
	String leftaddress, rightaddress;
	int port;

	public Server(String leftaddress, String rightaddress, int port) {
		this.leftaddress = leftaddress;
		this.rightaddress = rightaddress;
		this.port = port;
	}

	@Override
	public void run() {

		try {

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
			// server setup
			ServerSocket servSock = null;
			try {
				servSock = new ServerSocket(port);
			} catch (IOException e) {
				System.err.println(e);
			}
			Socket leftserver = null;
			Socket rightserver = null;
			try {
				Socket clientSockone = servSock.accept();
				if (clientSockone.getInetAddress().toString().equals(leftaddress)) {
					leftserver = clientSockone;
				} else {
					rightserver = clientSockone;
				}

				Socket clientSocktwo = servSock.accept();

				if (clientSocktwo.getInetAddress().toString().equals(rightaddress)) {
					rightserver = clientSocktwo;
				} else {
					leftserver = clientSocktwo;
				}

			} catch (IOException e) {
				System.out.println(e);
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
			// start
			while (true) {
				String temp = leftinputStr.readLine();
				while (temp != null) {
					leftread.add(temp);
				}
				temp = rightInputStr.readLine();
				while (temp != null) {
					rightread.add(temp);
				}
				for (String s : leftwrite) {
					leftOutputStr.writeBytes(s);
				}
				for (String s : rightwrite) {
					rightOutputStr.writeBytes(s);
				}

			}
		} catch (IOException  e) {
			e.printStackTrace();
		}

	}

};