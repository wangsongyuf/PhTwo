import java.io.IOException;
import java.io.StringBufferInputStream;
import java.lang.ref.PhantomReference;
import java.net.InetSocketAddress;
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
		String leftaddress;
		String rightaddress;
		State state=State.thinking;
		boolean leftChop = false;
		boolean rightChop = false;
		boolean cup = false;
		int port;
		int drinkaskcount=5;
		int drinktimecount=40;
		int sleepcount=40;
		if (args.length != 3) {
			System.out.println("bad input");
		}
		leftaddress = args[0];
		rightaddress = args[1];
		port = Integer.parseInt(args[2]);
		Server server=new Server(leftaddress,rightaddress,port);
		Random r = new Random();
		int rand=r.nextInt(10);
		while (true) {
			System.out.println(rand+","+state);
			if(state!=State.sleeping){
				while(!server.leftread.isEmpty()){
					String peek=server.leftread.peek();
					if(peek.equals("true")||peek.equals("false")){
						if(state==State.waitingCup||state==State.waitingLeftChop||state==State.waitingRightChop){
						break;}
					}else if(peek.equals("othertrue")){
						if(drinkaskcount==5){
							server.rightwrite.add(peek+",");
						}else{
							state=State.thristy;
						}
						server.leftread.pop();
					}else if(peek.equals("otherfalse")){
						if(drinkaskcount==5){
							server.rightwrite.add(peek+",");
						}else{
							drinkaskcount--;
						}
						server.leftread.pop();
					}else if(peek.equals("cup")){
						server.leftwrite.add(cup+",");
						server.rightwrite.add("othercup,");
						server.leftread.pop();
					}else if(peek.equals("othercup")){
						server.leftwrite.add("other"+cup+",");
						server.leftread.pop();
					}else if(peek.equals("chop")){
						server.leftwrite.add(leftChop+",");
						server.leftread.pop();
					}
				}
				while(!server.rightread.isEmpty()){
					String peek=server.rightread.peek();
					if(peek.equals("true")||peek.equals("false")){
						if(state==State.waitingCup||state==State.waitingLeftChop||state==State.waitingRightChop){
						break;}
					}else if(peek.equals("othertrue")){
						if(drinkaskcount==5){
							server.leftwrite.add(peek+",");
						}else{
							state=State.thristy;
						}
						server.rightread.pop();
					}else if(peek.equals("otherfalse")){
						if(drinkaskcount==5){
							server.leftwrite.add(peek+",");
						}else{
							drinkaskcount--;
						}
						server.rightread.pop();
					}else if(peek.equals("cup")){
						server.rightwrite.add(cup+",");
						server.leftwrite.add("othercup,");
						server.rightread.pop();
					}else if(peek.equals("othercup")){
						server.rightwrite.add("other"+cup+",");
						server.rightread.pop();
					}else if(peek.equals("chop")){
						server.rightwrite.add(rightChop+",");
						server.rightread.pop();
					}
				}
			}
			if((rand==4||rand==6) && state==State.hungry){
				if(rand==4){
					server.leftwrite.add("Chop,");
					state=State.waitingLeftChop;
				}
				else if (rand==6){
					server.rightwrite.add("Chop");
					state=State.waitingRightChop;
				}
			}else if(state==State.waitingRightChop){
				if(server.rightread.isEmpty()){
					continue;
				}
				String s=server.rightread.pop();
				if(s.contains("true")){
					state=State.hungry;
					leftChop=false;
					rightChop=false;
				}else{
					rightChop=true;
					if(leftChop){
						state=State.eating;
					}else {
						rand=4;
						continue;
					}
				}
			}else if(state==State.waitingLeftChop){
				if(server.leftread.isEmpty()){
					continue;
				}
				String s=server.leftread.pop();
				if(s.contains("true")){
					state=State.hungry;
					leftChop=false;
					rightChop=false;
				}else{
					leftChop=true;
					if(rightChop){
						state=State.eating;
					}else {
						rand=6;
						continue;
					}
				}
			}else if(rand==1&&state==State.thinking){
				state=State.hungry;
			}else if(rand==2&&state==State.eating){
				state=State.thinking;
				leftChop=false;
				rightChop=false;
			}else if(rand==3&&state==State.thinking){
				state=State.thristy;
			}else if(rand==5&&state==State.thristy){
				drinkaskcount--;
				server.rightwrite.add("cup,");
				state=State.waitingCup;
			}else if(drinkaskcount==0&&state==State.waitingCup){
				cup=true;
				drinkaskcount--;
			}
			else if(state==State.drinking&& drinktimecount>0){
				drinktimecount--;
			}else if(state==State.drinking&& drinktimecount<=0){
				state=state.sleeping;
				leftChop=false;
				rightChop=false;
				cup=false;
				drinktimecount=40;
			}else if(state==State.sleeping&& sleepcount>0){
				sleepcount--;
				server.leftread.clear();
				server.rightread.clear();
				server.leftwrite.clear();
				server.rightwrite.clear();
			}else if(state==State.sleeping&& sleepcount<=0){
				sleepcount=40;
				state=State.thinking;
			}else if(state==State.waitingCup){
				if(server.rightread.isEmpty()){
					continue;
				}
				String s=server.rightread.pop();
				if(s.equals("true")){
					state=State.thristy;
				}else {
					drinkaskcount--;
				}
			}
			 rand=r.nextInt(10);
			 Thread.sleep(1000L);
		}
		

	}

}
