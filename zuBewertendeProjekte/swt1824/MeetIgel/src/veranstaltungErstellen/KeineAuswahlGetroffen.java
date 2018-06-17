package veranstaltungErstellen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class KeineAuswahlGetroffen extends JDialog {

	// Variablendeklaration
    private javax.swing.JLabel lblTreffpunktAuswahl2;
    private javax.swing.JLabel lblTreffpunktAuswahl1;
    private javax.swing.JButton ok;
	
    // neuer Dialog
	public KeineAuswahlGetroffen(JFrame frame) {
		super(frame);
		initComponents();
		
		// Schlieﬂen des Dialogs 
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
	}
	
	// Initialisieren des Dialogs 
	private void initComponents() {

		lblTreffpunktAuswahl1 = new javax.swing.JLabel();
		ok = new javax.swing.JButton();
		lblTreffpunktAuswahl2 = new javax.swing.JLabel();

		// Schlieﬂen des Dialogs und Festlegen der Hintergrundfarbe
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schrift der JLabels
		lblTreffpunktAuswahl1.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		lblTreffpunktAuswahl1.setText("<html>Bitte w‰hle entweder <b>Treffpunkt vorgeben</b> oder <html>");
		lblTreffpunktAuswahl2.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		lblTreffpunktAuswahl2.setText("<html><b>Treffpunkt berechnen lassen</b> aus.<html>");

		// Text und Schrift des JButtons
		ok.setFont(new java.awt.Font("Tahoma", 1, 14));
		ok.setText("OK");

		// Platzierung der Komponenten im JDialog
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Ausrichtung der Komponenten
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(220, 220, 220)
	                        .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(80, 80, 80)
	                        .addComponent(lblTreffpunktAuswahl1))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(130, 130, 130)
	                        .addComponent(lblTreffpunktAuswahl2)))
	                .addContainerGap(79, Short.MAX_VALUE))
	        );
	        // Vertikale Ausrichtung der Elemente 
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap(30, Short.MAX_VALUE)
	                .addComponent(lblTreffpunktAuswahl1)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(lblTreffpunktAuswahl2)
	                .addGap(27, 27, 27)
	                .addComponent(ok)
	                .addGap(32, 32, 32))
	        );

	        pack();
	    }
}
