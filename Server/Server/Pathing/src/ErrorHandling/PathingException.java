package ErrorHandling;

public class PathingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String message;
	
	public PathingException(int id)
	{
		switch(id)
		{
			case 1: 
				this.id = id;
				this.message ="U and V are the same, which is not allowed in this definition";
			case 2:
				this.id = id;
				this.message ="Start- and endpoint are not reachable in this graph";
		}
	}
}
