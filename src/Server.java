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
				
				Socket leftserver = null;
				Socket rightserver = null;
				ServerSocket servSock = null;
				servSock = new ServerSocket(port);
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
					temp = rightinputStr.readLine();
					while (temp != null) {
						rightread.add(temp);
					}}

			} catch (IOException e) {
				System.out.println(e);
			}
			
			
		
		
				
	}
		

};