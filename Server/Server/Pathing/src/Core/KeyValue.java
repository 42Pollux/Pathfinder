package Core;

public class KeyValue <T1,T2> {
	public T1 Key;
	public T2 Value;
	
	public KeyValue(T1 key, T2 value)
	{
		this.Key = key;
		this.Value = value;		
	}
}
