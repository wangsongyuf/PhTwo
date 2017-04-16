import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.spi.CurrencyNameProvider;

public class main {
	public enum State {
		thinking, hungry, eating, thristy, drinking, sleeping, waitingLeftChop, waitingRightChop, waitingCup
	}
	static boolean leftChop = false;
	static boolean rightChop = false;
	static boolean cup = false;
	static State state = State.thinking;
	static int drinkaskcount = 5;
	static int drinktimecount = 40;
	static int sleepcount = 40;
	static int hungrycount=40;

	public static void main(String[] args)  {
		try{
		String leftaddress;
		String rightaddress;
		
		
		int port;
		
		if (args.length != 3) {
			System.out.println("bad inadd");
		}
		leftaddress = args[0];
		rightaddress = args[1];
		port = Integer.parseInt(args[2]);
		Server server = new Server(leftaddress, rightaddress, port);
		Client client = new Client(leftaddress, rightaddress, port);
		Thread t1 = new Thread(server);
		Thread t2 = new Thread(client);
		t1.start();
		t2.start();
		
		Thread t3=new Thread(new Runnable() {
			
			@Override
			public void run() {
				Scanner sc = new Scanner(System.in);
				boolean xxx = true;
				while (xxx) {
					String input = sc.next();
					leftChop = false;
					rightChop = false;
					cup=false;
					  drinkaskcount = 5;
					  drinktimecount = 40;
					  sleepcount = 40;
					  hungrycount=40;
					if (input.equals("thinking")) {
						state =State.thinking;

					} else if (input.equals("hungry")) {
						state = State.hungry;
					}
					
					else if (input.equals("thristy")) {
						state = State.thristy;
					}
					else if (input.equals("sleeping")) {
						state = State.sleeping;
					}
					
					else if (input.equals("gui")) {
						gui.run();
					}
					//TODO: add states

				}
				sc.close();
				
			}
		});
		t3.start();
	
		Thread.sleep(10000L);
		Random r = new Random();
		int rand = r.nextInt(10);
		while (hungrycount>0) {
			Thread.sleep(1000L);
			System.out.println("State: "+state);
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
							client.addright(peek + "\n");
						} else {
							state = State.thristy;
							drinkaskcount=5;
						}
						server.leftread.pop();
					} else if (peek.equals("otherfalse")) {
						if (drinkaskcount == 5) {
							client.addright(peek + "\n");
						} else {
							drinkaskcount--;
						}
						server.removeleft();
					} else if (peek.equals("cup")) {
						
						client.addleft(String.valueOf(cup) + "\n");
						client.addright("othercup\n");
						server.removeleft();
					} else if (peek.equals("othercup")) {
						client.addleft("other" + String.valueOf(cup) + "\n");
						if(drinkaskcount==5){
							client.addright("othercup\n");
						}
						
						server.removeleft();
					} else if (peek.equals("chop")) {
						client.leftwrite.add(String.valueOf(leftChop) + "\n");
						server.removeleft();
					} else {
						server.removeleft();
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
							client.addleft(peek + "\n");
						} else {
							state = State.thristy;
							drinkaskcount=5;
						}
						server.removeright();
					} else if (peek.equals("otherfalse")) {
						if (drinkaskcount == 5) {
							client.addleft(peek + "\n");
						} else {
							drinkaskcount--;
						}
						server.removeright();
					} else if (peek.equals("cup")) {
						client.addright(String.valueOf(cup) + "\n");
						client.addleft("othercup\n");
						server.removeright();
					} else if (peek.equals("othercup")) {
						client.addright("other" + String.valueOf(cup)+ "\n");
						if(drinkaskcount==5){
							client.addleft("othercup\n");
						}
						server.removeright();
					} else if (peek.equals("chop")) {
						client.addright(String.valueOf(rightChop) + "\n");
						server.removeright();
					} else {
						server.removeright();
					}
				}
			}
			if ((rand == 4 || rand == 6) && state == State.hungry) {
				if (rand == 4) {
					client.addleft("chop\n");
					state = State.waitingLeftChop;
				} else if (rand == 6) {
					client.addright("chop\n");
					state = State.waitingRightChop;
				}
			} else if (state == State.waitingRightChop) {
				if (server.rightread.isEmpty()) {
					continue;
				}
				String s = server.removeright();
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
						client.addleft("chop\n");
						continue;
					}
				}
			} else if (state == State.waitingLeftChop) {
				if (server.leftread.isEmpty()) {
					continue;
				}
				String s = server.removeleft();
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
						client.addright("chop\n");
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
				client.rightwrite.add("cup\n");
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
				String s = server.removeright();
				if (s.equals("true")) {
					state = State.thristy;
					drinkaskcount=5;
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

	}catch(Exception e){
		e.printStackTrace();
	}

}
}