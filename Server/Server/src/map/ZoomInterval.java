package map;

public class ZoomInterval {
	public byte base_zoom_level;
	public byte minimal_zoom_level;
	public byte maximal_zoom_level;
	public long absolute_start_pos;
	public long size_subfile;
	
	public ZoomInterval(byte base, byte min, byte max, long abs, long size) {
		this.base_zoom_level = base;
		this.minimal_zoom_level = min;
		this.maximal_zoom_level = max;
		this.absolute_start_pos = abs;
		this.size_subfile = size;
	}
}
