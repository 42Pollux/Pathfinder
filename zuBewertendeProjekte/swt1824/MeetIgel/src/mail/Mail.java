package mail;
 
import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class Mail
{
    public static void send(MailAccounts acc, String recipients
    		, String subject,
            String text) throws AddressException, MessagingException
    {
        // Properties über die Systemeigenschaften anlegen
        Properties properties = System.getProperties();
        
        properties.setProperty("mail.imap.ssl.enable", "true");
 
        // Server-Adresse hinzugefügen
        properties.setProperty("mail.smtp.host", acc.getSmtpHost());
         
        // In diesem Fall nicht notwendig (da der Standardport 25 ist), aber
        // dennoch wissenswert ist das Setzen des Serverports
        // (für den Fall das beispielsweise die E-Mail verschlüsselt versendet werden soll)
        properties.setProperty("mail.smtp.port", String.valueOf(acc.getPort()));
 
        // In der Regel wird nach Authentifizierungsdaten gefragt, weshalb
        // dies in den Properties expliziert angegeben werden muss
        properties.setProperty("mail.smtp.auth", "true");
         
        // Eine (Standard)Session wird erstellt.
        // wird keine Authentifizierung benötigt, wird "null" als Attribut übertragen
        Session session = Session.getDefaultInstance(properties, acc.getPasswordAuthentication());
    
//        final javax.mail.Authenticator auth = new javax.mail.Authenticator() {
//     	   @Override
//     	   public PasswordAuthentication getPasswordAuthentication() {
//     	      return new PasswordAuthentication("meetigel@gmx.de", "1824igel");
//     	   }
//     	};
//     	
//     	Session session = Session.getDefaultInstance(properties, auth);
        
        //Store store = session.getStore("imap");
        //store.connect("imap.gmail.com", "meetigelinfo@gmail.com", "1824igel");
        
        
//         ///////////////////
        Properties props = System.getProperties();
        props.put("mail.smtp.user","meetigelinfo@gmail.com"); 
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "25"); 
        props.put("mail.debug", "true"); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable","true"); 
        props.put("mail.smtp.EnableSSL.enable","true");

        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
        props.setProperty("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.port", "465");   
        props.setProperty("mail.smtp.socketFactory.port", "465"); 
        
        Session session2 = Session.getDefaultInstance(props, acc.getPasswordAuthentication());
 
        // Eine neue Nachricht wird erzeugt
        MimeMessage msg = new MimeMessage(session2);
 
        // Von wem kommt die E-Mail?
        msg.setFrom(new InternetAddress(acc.getEmail()));
 
        // Wohin soll die Reise gehen?
        // CC geht beispielsweise an Message.RecipientType.CC
        //msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipient, false));
        
        //msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("meetigelinfo@gmail.com"));

        msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipients));
 
        // Betreff
        msg.setSubject(subject);
         
        // Nachricht
        msg.setText(text);
         
        // E-Mail versenden
        Transport.send(msg);
    }
}