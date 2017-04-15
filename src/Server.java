import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

class Server implements Runnable {
	public ArrayDeque<String> leftread = new ArrayDeque<>();
	public ArrayDeque<String> rightread = new ArrayDeque<>();
	public ArrayDeque<String> leftwrite = new ArrayDeque<>();
	public ArrayDeque<String> rightwrite = new ArrayDeque<>();
	String leftaddress,rightaddress;
	int port;
	
	public Server(String leftaddress, String rightaddress, int port) {
		this.leftaddress=leftaddress;
		this.rightaddress=rightaddress;
		this.port=port;
	}

	@Override
	public void run() {

		SocketChannel left;
		try {
			left = SocketChannel.open();
			left.connect(new InetSocketAddress(leftaddress, port));
			SocketChannel right = SocketChannel.open();
			right.connect(new InetSocketAddress(rightaddress, port));
			while (!left.finishConnect() && !right.finishConnect()) {
				ByteBuffer buf = ByteBuffer.allocate(512);
				buf.clear();
				int bytesRead = left.read(buf);
				if (bytesRead != -1) {
					for (String s : StandardCharsets.UTF_8.decode(buf).toString().split(",")) {
						leftread.add(s);
					}
					buf.clear();
				}
				buf.clear();
				bytesRead = right.read(buf);
				if (bytesRead != -1) {
					for (String s : StandardCharsets.UTF_8.decode(buf).toString().split(",")) {
						rightread.add(s);
					}
					buf.clear();
				}
				for (String s : leftwrite) {
					buf.clear();
					buf.put(s.getBytes());
					buf.flip();
					left.write(buf);
					Thread.sleep(1000L);
				}
				for (String s : rightwrite) {
					buf.clear();
					buf.put(s.getBytes());
					buf.flip();
					right.write(buf);
					Thread.sleep(1000L);
				}

			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

};