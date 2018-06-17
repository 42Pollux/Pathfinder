package controlclasses;

import java.io.IOException;
import java.sql.*;


import controlclasses.DistanceTime;
public class Berechnung {
	 
	
	/*
	 * Zusammenfassende Methode der Klasse Berechnung, bei der ein 
	 * sortiertes Array mit ID der besten 6 Treffpunkte ausgegeben wird
	 */
	public static int[] berechnen(String kuerzel) throws IOException {
		int liste[]=sortierteTP(durchschnitt(getZeitmatrix(kuerzel)));
		if (liste==null)return null;
		return liste;
		
	}

	/*
	 * Methode zum generieren einer Matrix mit Anfahrtszeiten
	 * TODO: Abfragen formulieren (eingabe Kürzel), Datenbank-Adresse
	 */
	public static double[][] getZeitmatrix(String kuerzel) throws IOException {
		double zeitMatrix[][] =null;
		try
	    {
			Class.forName("com.mysql.jdbc.Driver");	 //
	    }
		catch ( ClassNotFoundException e )
	    {
			System.err.println( "Keine Treiber-Klasse!" );
			return null;
	    }
		Connection con = null;


	    try
	    {	    	
	    	con =  DriverManager.getConnection( "jdbc:mysql://139.30.96.135/swt1821DB?user=root&password=root"); 
	    	Statement stmt = con.createStatement();
	    	
	    	String queryAtmo = "SELECT atmosphere FROM Gruppe WHERE kuerzel=\""+kuerzel+"\";";
	    	ResultSet rsAtmo = stmt.executeQuery( queryAtmo );
	    	String am=rsAtmo.getString(1); //atmospäre der Gruppe
	    	rsAtmo.close();

	      
	    	String queryNutzer = "SELECT Standort, Vehikel_Name FROM Nutzer natural join ist_in WHERE kuerzel=\""+kuerzel+"\";";// Tabelle aus vehikel und standort
			ResultSet rsNutzer = stmt.executeQuery( queryNutzer );
			
			ResultSet rsTreffpunkte=null;

			if(am=="gesellig"||am=="gruppenarbeit"){//TODO: evtl datenbank adresse korrigieren
				String queryTreffpunkte = "SELECT id`swt1821DB`.`standort`, adresse_strasse FROM Standort WHERE Atmosphaere.atmosphere=\""+am+"\";"; //Tabelle aus Treffpunkt ID und Adresse und am== gesellig oder gruppenarbeit 
				rsTreffpunkte = stmt.executeQuery( queryTreffpunkte );
			}
			else {
				String queryTreffpunkte = "SELECT id`swt1821DB`.`standort`, adresse_strasse FROM Standort;"; //Tabelle aus Treffpunkt ID und Adresse und am==einzelarbeit
				rsTreffpunkte = stmt.executeQuery( queryTreffpunkte );
			}
			
			
			/*
			 * Es wird eine Matrix erstellt, in die später die Zeiten eingetragen werden,
			 * die jeder nutzer zu einem möglichen Treffpunkt braucht
			 * i-Spalten, j-Zeilen
			 * i startet bei 1 da 0-te Spalte für SID reserviert wird
			 */
			int i=1,j=0;
			while(rsTreffpunkte.next())j++; //Zeilen werden ermittelt
			while(rsNutzer.next())i++; //Spalten werden ermittelt
			rsTreffpunkte.first(); //setzt cursor auf 1. Position
			rsNutzer.first();
			
			
			zeitMatrix = new double[i][j];
			
			
			int zeilen=zeitMatrix[0].length;
			int spalten=zeitMatrix.length;
			String[] adressen = new String[zeilen];
			/*
			 * ID des Treffpunktes wird in die erste Spalte der Matrix eingetragen
			 * WICHTIG FÜR DIE ZUORNDNUNG
			 */
			for (j=0;j<zeilen;j++) {
				zeitMatrix[0][j]=rsTreffpunkte.getInt(1); //ID wird in erste Spalte der zeitMatrix eingetragen
				adressen[j]=rsTreffpunkte.getString(2); //Treffpunktadressen werden in adressen[] eingetragen
				rsTreffpunkte.next();
			}
			
			
			/*
			 * Es werden die Reisedauern der Nutzer zu den Treffpunkten in zeitMatrix eingetragen
			 * zur Vereinfachunug der DB wird "Rostock" ergänzt
			 */
			for (i=1;i<spalten;i++) {	
				for (j=0;j<zeilen;j++) {
					zeitMatrix[i][j]=new DistanceTime().getZeit(rsNutzer.getString(1)+", Rostock" //Standort Nutzer
							,adressen[j]+", Rostock" //Treffpunkt j
							,rsNutzer.getString(2)); //Vehikel von Nutzer
					rsNutzer.next(); //Springt eine Zeile tiefer
				}
			}
			rsNutzer.close();
			rsTreffpunkte.close();

			stmt.close();
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
		
		return zeitMatrix;
		
	}
	
	

	
	
	/*
	 * Erstellen einer 2xn Matrix mit SID in 0-ter Spalte und durchschnittlicher Zeit in 1-ter Spalte
	 */
	public static double[][] durchschnitt(double[][] matrix){
		if (matrix==null)return null;
		double durchschnittListe[][]=new double[2][matrix[0].length];
		int n =matrix.length-1;
		for (int j=0;j<durchschnittListe[0].length;j++) {
			durchschnittListe[0][j]=matrix[0][j]; //SID wird eingetragen
			for (int i=1;i<durchschnittListe.length;i++) {
				for (int k=1; k<matrix.length;k++) {
					durchschnittListe[1][j]+=matrix[k][j]; //Zeiten werden Aufaddiert
				}
			}
		}
		for (int j=0;j<durchschnittListe[0].length;j++) {
			durchschnittListe[1][j]/=n; //durchschnitt wird ausgerechnet
			//System.out.println(durchschnittListe[1][j]);
		}
		
		
		return durchschnittListe;
	}
	
	

	
	
	
	
	
	/*
	 * Methode für die Sortierung einer 2xn Matrix nach kleinster durchschnittszeit
	 * Rückgabe eines Arrays das nur SID enthält
	 * das Array soll 6 Felder haben
	 */
	public static int[] sortierteTP( double[][] durchschnittListe) {
		if(durchschnittListe==null)return null;
		
		for (int i = 0; i < durchschnittListe[0].length - 1; i++) {
			for (int j = i + 1; j < durchschnittListe[0].length; j++) {
				if (durchschnittListe[1][i] > durchschnittListe[1][j]) {
					double temp1 = durchschnittListe[1][i];
					durchschnittListe[1][i] = durchschnittListe[1][j];
					durchschnittListe[1][j] = temp1;
					double temp0 = durchschnittListe[0][i];
					durchschnittListe[0][i] = durchschnittListe[0][j];
					durchschnittListe[0][j] = temp0;
				}
			}
		}
		//für den Fall das eine Matrix mit weniger als 6 Zeilen übergeben wird soll ein array mit weniger Feldern übergeben werden
		if(durchschnittListe[0].length<6) {
			int[] auswahlKleiner=new int[durchschnittListe[0].length];
			for (int i = 0; i < durchschnittListe[0].length; i++) {
				auswahlKleiner[i]=(int)durchschnittListe[0][i];
			}
			return auswahlKleiner; //rückgabe des kleineren Arrays
		}
		int[] auswahl=new int[6];
		for (int i = 0; i < 6; i++) {
			auswahl[i]=(int)durchschnittListe[0][i];
		}
		return auswahl; //rückgabe der SID in sortierter Reihenfolge (alternativ auch Rückgabe von durchschnittListe)
	}
}
