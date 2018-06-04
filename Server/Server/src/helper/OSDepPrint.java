package helper;

public class OSDepPrint {

	private static boolean is_windows = false;
	private static boolean is_debug = false;
	private static boolean show_pbar = false;
	
	public static void initialize(){
		if(System.getProperty("os.name").contains("Windows")) {
			is_windows = true;
		}
	}
	
	public static void setDebug(boolean debug) {
		is_debug = debug;
	}
	
	public static void setPBar(boolean pbar) {
		show_pbar = pbar;
	}
	
	public static void error(String msg){
		if(is_windows){
			System.out.println(" [ERROR] " + msg);
		} else {
			System.out.println(" [\033[0;31mERROR\033[0m] " + msg);
		}
	}
	
	public static void error(String msg, int ref){
		if(is_windows){
			System.out.println(" [ #" + ref + " ] " + "[ERROR] " + msg);
		} else {
			System.out.println(" [ #" + ref + " ] " + "[\033[0;31mERROR\033[0m] " + msg);
		}
	}
	
	public static void net(String msg){
		if(is_windows){
			System.out.println(" [NET] " + msg);
		} else {
			System.out.println(" [\033[0;32mNET\033[0m] " + msg);
		}
	}
	
	public static void net(String msg, int ref){
		if(is_windows){
			System.out.println(" [ #" + ref + " ] " + "[NET] " + msg);
		} else {
			System.out.println(" [ #" + ref + " ] " + "[\033[0;32mNET\033[0m] " + msg);
		}
	}
	
	public static void info(String msg){
		if(is_windows){
			System.out.println(" [INFO] " + msg);
		} else {
			System.out.println(" [INFO] " + msg);
		}
	}
	
	public static void info(String msg, int ref){
		if(is_windows){
			System.out.println(" [ #" + ref + " ] " + "[INFO] " + msg);
		} else {
			System.out.println(" [ #" + ref + " ] " + "[INFO] " + msg);
		}
	}
	
	public static void debug(String msg){
		if(is_debug){
			if(is_windows){
				System.out.println(" [DEBUG] " + msg);
			} else {
				System.out.println(" [\033[0;33mDEBUG\033[0m] " + msg);
			}
		}
	}
	
	public static void debug(String msg, int ref){
		if(is_debug){
			if(is_windows){
				System.out.println(" [ #" + ref + " ] " + "[DEBUG] " + msg);
			} else {
				System.out.println(" [ #" + ref + " ] " + "[\033[0;33mDEBUG\033[0m] " + msg);
			}
		}
	}
	
	public static void printProgress(long current, long maximum, float dlspeed, int ref){
		if(show_pbar){
			long step = maximum/40;
			if(step<1) step = 1;
			String line = "";
			for(long i = 0; i<40; i++){
				if(i<=(current/step)){
					line = line + "=";
				} else {
					line = line + " ";
				}
			}
			float max = (float) maximum;
			float percent = (100.0f / max) * (float)current;
			if(is_windows){
				System.out.printf(" [ #" + ref + " ] " + "[DEBUG] Uploading [" + line + "] %.1f%%  %.1f KB/s\r", percent, dlspeed);
			} else {
				System.out.printf(" [ #" + ref + " ] " + "[\033[0;33mDEBUG\033[0m] Uploading [" + line + "] %.1f%%  %.1f KB/s\r", percent, dlspeed);
			}
		}
	}
	
	public static void printProgressStop(){
		if(show_pbar) {
			System.out.printf("\n");
		}
	}
}
