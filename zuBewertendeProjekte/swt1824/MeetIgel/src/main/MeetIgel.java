package main;

import javax.swing.JFrame;

import login.Login;

public class MeetIgel extends JFrame {

	// neuer Frame
	public MeetIgel() {
		
	}

	// Main Methode 
	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MeetIgel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MeetIgel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MeetIgel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MeetIgel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		// Programm starten, indem ein Frame erstellt wird, in dem die
		// Login-Oberfl‰che angezeigt wird
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();

					// Login-Oberfl‰che anzeigen
					frame.getContentPane().add(new Login(frame));
					frame.setTitle("Login");
					frame.setResizable(false);
					frame.pack();
					frame.setVisible(true);
					// Frame Schlieﬂen und Programm beenden
					frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
