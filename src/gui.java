import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;



public class gui {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnNewButton = new JButton("Hungry");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.state=main.State.hungry;
				main.leftChop=false;
				main.rightChop=false;
				main.cup=false;
			}
		});
		frame.getContentPane().add(btnNewButton, BorderLayout.WEST);
		
		JButton btnNewButton_1 = new JButton("Thinking");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.state=main.State.thinking;
				main.leftChop=false;
				main.rightChop=false;
				main.cup=false;
			}
		});
		frame.getContentPane().add(btnNewButton_1, BorderLayout.EAST);
		
		JButton btnThi = new JButton("Thristy");
		btnThi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.state=main.State.thristy;
				main.leftChop=false;
				main.rightChop=false;
				main.cup=false;
			}
		});
		frame.getContentPane().add(btnThi, BorderLayout.NORTH);
		
		JButton btnSleeping = new JButton("Sleeping");
		btnSleeping.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.state=main.State.sleeping;
				main.leftChop=false;
				main.rightChop=false;
				main.cup=false;
			}
		});
		frame.getContentPane().add(btnSleeping, BorderLayout.SOUTH);
	}

}
