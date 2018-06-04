package server;

import java.util.Calendar;
import java.util.UUID;

public class UUIDManager {
	
	public static String generateNewUUID() {
		UUID uid = UUID.randomUUID();
		char[] str = uid.toString().toCharArray();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		char[] year_str = String.valueOf(year).toCharArray();
		for(int i=0; i<2; i++) {
			str[i] = year_str[i+2];
		}
		
		// TODO sql validate
		
		return String.valueOf(str);
	}
	
	public static boolean validateUUID(String str) {
		return true;
	}

}
