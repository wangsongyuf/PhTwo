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

	@Override
	public void run() {

		try {

			// Socket leftserver = null;
			// Socket rightserver = null;
			// ServerSocket servSock = null;
			// servSock = new ServerSocket(port);
			// Socket clientSockone = servSock.accept();
			// if
			// (clientSockone.getInetAddress().toString().equals(leftaddress)) {
			// leftserver = clientSockone;
			// } else {
			// rightserver = clientSockone;
			// }
			//
			// Socket clientSocktwo = servSock.accept();
			//
			// if
			// (clientSocktwo.getInetAddress().toString().equals(rightaddress))
			// {
			// rightserver = clientSocktwo;
			// } else {
			// leftserver = clientSocktwo;
			// }
			// BufferedReader leftinputStr;
			// DataOutputStream leftoutputStr;
			// String rightinputLine;
			// BufferedReader rightinputStr;
			// DataOutputStream rightoutputStr;
			// leftinputStr = new BufferedReader(new
			// InputStreamReader(leftserver.getInputStream()));
			// leftoutputStr = new
			// DataOutputStream(leftserver.getOutputStream());
			// rightinputStr = new BufferedReader(new
			// InputStreamReader(rightserver.getInputStream()));
			// rightoutputStr = new
			// DataOutputStream(rightserver.getOutputStream());

			ServerSocketChannel server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(port));
			server.configureBlocking(false);

			SocketChannel leftserver = null;
			SocketChannel rightserver = null;
			System.out.println("try to get leftright");
			SocketChannel clientSockone = server.accept();
			while (clientSockone == null) {
				clientSockone = server.accept();
			}
			if (clientSockone.getRemoteAddress().toString().equals(leftaddress)) {
				leftserver = clientSockone;
			} else {
				rightserver = clientSockone;
			}

			SocketChannel clientSocktwo = server.accept();
			while (clientSocktwo == null) {
				clientSocktwo = server.accept();
			}
			if (clientSocktwo.getRemoteAddress().toString().equals(rightaddress)) {
				rightserver = clientSocktwo;
			} else {
				leftserver = clientSocktwo;
			}

			// start
			System.out.println("read");
			while (true) {
				ByteBuffer buf = ByteBuffer.allocate(1024);
				buf.clear();
				leftserver.read(buf);
				System.out.println(StandardCharsets.UTF_8.decode(buf).toString());
				for (String s : StandardCharsets.UTF_8.decode(buf).toString().split("\n")) {
					// System.out.println("leftread:"+s);
					leftread.add(s);
				}
				buf.clear();

				rightserver.read(buf);

				System.out.println(StandardCharsets.UTF_8.decode(buf).toString());
				for (String s : StandardCharsets.UTF_8.decode(buf).toString().split("n")) {
					// System.out.println("rightread:"+s);
					rightread.add(s);
				}
				buf.clear();

			}

		} catch (IOException e) {
			System.out.println(e);
		}

	}

};