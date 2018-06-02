package ErrorHandling;

public class AccessPointException extends Exception {
	
	public String ErrorMessage = "";
	public int ErrorCode;
	
	public AccessPointException(int errorCode)
	{
		switch(errorCode){
			case 0: 
				ErrorCode = errorCode;
				ErrorMessage = "couldn't find vertex with this name";
				break;
		}
		
			
	}

}
