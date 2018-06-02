package ErrorHandling;

public class PathingInterfaceException extends Exception{
	protected int ID;
	protected String Message;
	
	public PathingInterfaceException(int id)
	{
		switch(id)
		{
			case 0:
				this.ID = id;
				this.Message = "to less input points";
		}
	}
	
	public int getID()
	{
		return this.ID;
	}
	
	public String getMessage()
	{
		return this.Message;
	}
}
