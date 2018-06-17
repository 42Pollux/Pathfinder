package controlclasses;

import java.io.IOException;
import java.io.OutputStream;

public class Answer  extends OutputStream {

	boolean bestanden;
	
	public Answer() throws Exception{
		
	}

	@Override
	public void write(int b) throws IOException {
		if(b>0)
			bestanden=true;
		else
			bestanden=false;
	}


	
}
