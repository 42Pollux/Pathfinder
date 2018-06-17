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

public class Abschluss extends JDialog {
	
	// Variablendeklaration
	private javax.swing.JLabel text;
	private javax.swing.JButton ok;
	
	// neuer Dialog
	public Abschluss(JFrame frame){
		super(frame);
		initComponents();
		
		// Schließen des Dialogs
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	// Dialog initialisieren 
	private void initComponents() {

		text = new javax.swing.JLabel();
		ok = new javax.swing.JButton();

		//Schließen des Dialogs und Festlegen der Hintergrundfarbe
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		//Festlegen der Schriftart-und größe, sowie des Textes des JLabels
		text.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		text.setText("Veranstaltung erstellen abgeschlossen.");

		//Festlegen der Schriftart-und größe, sowie des Textes des JButtons
		ok.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		ok.setText("OK");

		
		//Platzierung der Komponenten im JDialog 
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Anordnung der Elemente
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(200, 200, 200)
	                        .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(109, 109, 109)
	                        .addComponent(text)))
	                .addContainerGap(109, Short.MAX_VALUE))
	        );
	        // Vertikale Anordnung der Elemente
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGap(33, 33, 33)
	                .addComponent(text)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
	                .addComponent(ok)
	                .addGap(32, 32, 32))
		);
		pack();
	}
}
