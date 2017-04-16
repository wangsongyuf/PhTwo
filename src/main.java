import java.io.IOException;
import java.io.StringBufferInputStream;
import java.lang.ref.PhantomReference;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;

public class main {
	public enum State {
		thinking, hungry, eating, thristy, drinking, sleeping, waitingLeftChop, waitingRightChop, waitingCup
	}

	public static void main(String[] args) throws InterruptedException {
		String leftputress;
		String rightputress;
		State state = State.thinking;
		boolean leftChop = false;
		boolean rightChop = false;
		boolean cup = false;
		int port;
		int drinkaskcount = 5;
		int drinktimecount = 40;
		int sleepcount = 40;
		int hungrycount=40;
		if (args.length != 3) {
			System.out.println("bad input");
		}
		leftputress = args[0];
		rightputress = args[1];
		port = Integer.parseInt(args[2]);
		Server server = new Server(leftputress, rightputress, port);
		Client client = new Client(leftputress, rightputress, port);
		Thread t1 = new Thread(server);
		Thread t2 = new Thread(client);
		t1.start();
		t2.start();
	
		Thread.sleep(10000L);
		Random r = new Random();
		int rand = r.nextInt(10);
		while (hungrycount>0) {
			System.out.println("drink count:"+drinkaskcount);
			Thread.sleep(1000L);
			System.out.println(rand + "," + state);
			if (state != State.sleeping) {
				while (!server.leftread.isEmpty()) {
					String peek = server.leftread.peek();
//					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//					System.out.println("peek is: "+peek);
//					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					if (peek.equals("true") || peek.equals("false")) {
						if (state == State.waitingCup || state == State.waitingLeftChop
								) {
							break;
						}
					} else if (peek.equals("othertrue")) {
						if (drinkaskcount == 5) {
							client.rightwrite.put(peek + "\n");
						} else {
							state = State.thristy;
							drinkaskcount=5;
						}
						server.leftread.take();
					} else if (peek.equals("otherfalse")) {
						if (drinkaskcount == 5) {
							client.rightwrite.put(peek + "\n");
						} else {
							drinkaskcount--;
						}
						server.leftread.take();
					} else if (peek.equals("cup")) {
						if(state==State.waitingCup){
							state=State.thristy;
							drinkaskcount=5;
						}
						client.leftwrite.put(String.valueOf(cup) + "\n");
						client.rightwrite.put("othercup\n");
						server.leftread.take();
					} else if (peek.equals("othercup")) {
						client.leftwrite.put("other" + String.valueOf(cup) + "\n");
						if(drinkaskcount==5){
							client.rightwrite.put("othercup\n");
						}
						if(state==State.waitingCup){
							state=State.thristy;
							drinkaskcount=5;
						}
						server.leftread.take();
					} else if (peek.equals("chop")) {
						client.leftwrite.put(String.valueOf(leftChop) + "\n");
						server.leftread.take();
					} else {
						server.leftread.take();
					}
				}
				while (!server.rightread.isEmpty()) {
					String peek = server.rightread.peek();
					if (peek.equals("true") || peek.equals("false")) {
						if (state == State.waitingCup 
								|| state == State.waitingRightChop) {
							break;
						}
					} else if (peek.equals("othertrue")) {
						if (drinkaskcount == 5) {
							client.leftwrite.put(peek + "\n");
						} else {
							state = State.thristy;
							drinkaskcount=5;
						}
						server.rightread.take();
					} else if (peek.equals("otherfalse")) {
						if (drinkaskcount == 5) {
							client.leftwrite.put(peek + "\n");
						} else {
							drinkaskcount--;
						}
						server.rightread.take();
					} else if (peek.equals("cup")) {
						client.rightwrite.put(String.valueOf(cup) + "\n");
						client.leftwrite.put("othercup\n");
						server.rightread.take();
					} else if (peek.equals("othercup")) {
						client.rightwrite.put("other" + String.valueOf(cup)+ "\n");
						if(drinkaskcount==5){
							client.leftwrite.put("othercup\n");
						}
						server.rightread.take();
					} else if (peek.equals("chop")) {
						client.rightwrite.put(String.valueOf(rightChop) + "\n");
						server.rightread.take();
					} else {
						server.rightread.take();
					}
				}
			}
			if ((rand == 4 || rand == 6) && state == State.hungry) {
				if (rand == 4) {
					client.leftwrite.put("chop\n");
					state = State.waitingLeftChop;
				} else if (rand == 6) {
					client.rightwrite.put("chop\n");
					state = State.waitingRightChop;
				}
			} else if (state == State.waitingRightChop) {
				if (server.rightread.isEmpty()) {
					continue;
				}
				String s = server.rightread.take();
				if (s.contains("true")) {
					state = State.hungry;
					leftChop = false;
					rightChop = false;
				} else {
					rightChop = true;
					if (leftChop) {
						state = State.eating;
					} else {
						rand = 4;
						state=State.waitingLeftChop;
						client.leftwrite.put("chop\n");
						continue;
					}
				}
			} else if (state == State.waitingLeftChop) {
				if (server.leftread.isEmpty()) {
					continue;
				}
				String s = server.leftread.take();
				if (s.contains("true")) {
					state = State.hungry;
					leftChop = false;
					rightChop = false;
				} else {
					leftChop = true;
					if (rightChop) {
						state = State.eating;
					} else {
						rand = 6;
						state=State.waitingRightChop;
						client.rightwrite.put("chop\n");
						continue;
					}
				}
			} else if (rand == 1 && state == State.thinking) {
				state = State.hungry;
			} else if (rand == 2 && state == State.eating) {
				state = State.thinking;
				leftChop = false;
				rightChop = false;
			} else if (rand == 3 && state == State.thinking) {
				state = State.thristy;
			} else if (rand == 5 && state == State.thristy) {
				drinkaskcount--;
				client.rightwrite.put("cup\n");
				state = State.waitingCup;
			} else if (drinkaskcount == 0 && state == State.waitingCup) {
				cup = true;
				state=State.drinking;
				drinkaskcount=5;
				drinktimecount=40;
			} else if (state == State.drinking && drinktimecount > 0) {
				drinktimecount--;
			} else if (state == State.drinking && drinktimecount <= 0) {
				state = state.sleeping;
				leftChop = false;
				rightChop = false;
				cup = false;
				drinktimecount = 40;
			} else if (state == State.sleeping && sleepcount > 0) {
				sleepcount--;
				server.leftread.clear();
				server.rightread.clear();
				client.leftwrite.clear();
				client.rightwrite.clear();
			} else if (state == State.sleeping && sleepcount <= 0) {
				sleepcount = 40;
				state = State.thinking;
			} else if (state == State.waitingCup) {
				if (server.rightread.isEmpty()) {
					continue;
				}
				String s = server.rightread.take();
				if (s.equals("true")) {
					state = State.thristy;
				} else {
					drinkaskcount--;
				}
			}else if(state==State.hungry){
				hungrycount--;
			}
			else if(state==State.eating){
				hungrycount=40;
			}
			rand = r.nextInt(10);
		}
		System.out.println("RIP");

	}

}
