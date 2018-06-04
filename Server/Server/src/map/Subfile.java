package map;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class Subfile {
	
	private ZoomInterval z_interval;
	private int tile_count;
	private long[] offsets;
	private long[] tile_sizes;
	private boolean[] covered_by_water;
	private boolean debug;
	
	private byte[] empty_tile;
	
	public Subfile(ZoomInterval intv, int _tile_count, long[] _offsets, long[] sizes, boolean[] water, boolean _debug) {
		this.z_interval = intv;
		this.tile_count = _tile_count;
		this.offsets = _offsets;
		this.tile_sizes = sizes;
		this.covered_by_water = water;
		this.debug = _debug;
		this.empty_tile = createEmptyTile(intv.maximal_zoom_level-intv.minimal_zoom_level+1);
	}
	
	private byte[] createEmptyTile(int levels) {
		byte[] ret;
		if(debug) {
			ret = new byte[levels*2+1+32];
		} else {
			ret = new byte[levels*2+1];
		}
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0;
		}
		// TODO add 32byte header for debug mode
		return ret;
	}
	
	public int getTileCount() {
		return this.tile_count;
	}
	
	public int getBaseZoom() {
		return z_interval.base_zoom_level;
	}
	
	public long getSubfileSize() {
		return z_interval.size_subfile;
	}
	
	public long[] getTileSizes() {
		return this.tile_sizes;
	}
	
	public byte[] getEmptyTile() {
		return empty_tile;
	}

}
