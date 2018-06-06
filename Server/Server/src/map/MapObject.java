/**
 * 
 * @author pollux
 *
 */
package map;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class MapObject {
	FileInputStream in_f;
	BoundingBox map_bounding;
	boolean flag_debug = false;
	String path_to_file;

	// header
	private final int HEADER_SIZE_OFFSET = 24;
	// header value fields
	private byte[] magic_bytes = new byte[20];
	private int header_size = 0;
	private int header_file_version;
	private long header_file_size;
	private long header_date_of_creation;
	private BoundingBox header_bounding_box;
	private short header_tile_size;
	private String header_projection;
	private byte header_flags;
	private int[] header_map_start_pos = new int[2];
	private byte header_start_zoom_level;
	private String header_lang_pref;
	private String header_comment;
	private String header_created_by;
	private String[] header_poi_tags;
	private String[] header_way_tags;
	private byte header_zoom_intervals;
	private ZoomInterval[] z_intervals;
	
	private int[] tile_count;
	private Subfile[] subfiles;
	
	private final int SUPER_DUPER_MAGIC_4 = 4; // doesn't work without this, idk why...

	public MapObject(String _path_to_file) {
		try {
			this.in_f = new FileInputStream(_path_to_file);
			this.path_to_file = _path_to_file;
			readHeader(in_f);
			tile_count = new int[header_zoom_intervals];
			//empty_tile = new byte[header_zoom_intervals][1];
			for(int i=0; i<tile_count.length; i++) {
				tile_count[i] = getTileCount(z_intervals[i].base_zoom_level, header_bounding_box);
				//empty_tile[i][0] = 
			}
			subfiles = new Subfile[header_zoom_intervals];
			createSubfiles(in_f, z_intervals, tile_count);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// close input streams
		try {
			in_f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String generateSubmap(double[] box, int start_zoom, String map_name) throws Exception {
		FileInputStream in;
		BoundingBox bb = new BoundingBox(box);
		long[] ret_tile_counter = new long[header_zoom_intervals];
		long[] ret_tile_size_summary = new long[header_zoom_intervals];
		List<boolean[]> ret_covered_by_water= new ArrayList<boolean[]>();
		long[] ret_size = new long[header_zoom_intervals];
		
		// skip header
		in = new FileInputStream(path_to_file);
		in.skip(header_size);
		
		// calculate the subfile sizes
		for(int i=0; i<header_zoom_intervals; i++) {
			long tile_counter = 0;
			long tile_size_summary = 0;
			
			//in.skip(subfiles[i].getTileCount()*5);
			byte[] index_entry = new byte[5];
			boolean[] water = new boolean[subfiles[i].getTileCount()];
			for(int j=0; j<subfiles[i].getTileCount(); j++) {
				in.read(index_entry);
				if((index_entry[0]&128)>0) {
					water[j] = true;
				} else {
					water[j] = false;
				}
				
			}
			
			int[][] tile_interval = getTileInterval(subfiles[i].getBaseZoom(), bb);
			int[] tile_map = getMapSize(subfiles[i].getBaseZoom(), header_bounding_box);
			int[] map_offset = getXYOffset(subfiles[i].getBaseZoom(), header_bounding_box);
			for(int j=0; j<tile_map[1]; j++) {
				for(int k=0; k<tile_map[0]; k++) {
					if((k>=tile_interval[0][0]-map_offset[0])
							&&(k<=tile_interval[0][1]-map_offset[0])
							&&(j>=tile_interval[1][0]-map_offset[1])
							&&(j<=tile_interval[1][1]-map_offset[1])) {
						// read tile
						tile_size_summary += subfiles[i].getTileSizes()[j*tile_map[0]+k];
						in.skip(subfiles[i].getTileSizes()[j*tile_map[0]+k]);
						tile_counter++;
					} else {
						// skip tile
						in.skip(subfiles[i].getTileSizes()[j*tile_map[0]+k]);
					}
				}
			}
			//System.out.println("Subfile " + i + ": " + tile_counter + " tiles, " + tile_size_summary + " bytes ---- reduced from " + z_intervals[i].size_subfile + " bytes");
			ret_tile_counter[i] = tile_counter;
			ret_tile_size_summary[i] = tile_size_summary;
			ret_size[i] = 5*tile_counter + tile_size_summary;
			//System.out.println("tile counter: " + tile_counter);
			ret_covered_by_water.add(water);
		}
		
		in.close();
		
		
		// write header
		//String file_dest = MapObject.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		//file_dest = file_dest.substring(0,  file_dest.length()-10); //server.jar 10 chars
		String file_dest = "/home/michael/pathfinder";
		file_dest = file_dest + "/tmp/" + map_name;
		FileOutputStream out = new FileOutputStream(file_dest);
		int new_header_size = 73 - 20 + SUPER_DUPER_MAGIC_4 + getStringSize(header_projection) + getStringSize(header_comment) + getStringSize("Pathfinder Server Application") + (header_zoom_intervals*19);
		
		//System.out.println("projection in bytes: " + getStringSize(header_projection) + "\nheader_comment in bytes: " + getStringSize(header_comment) + "\ncreated_by in bytes: " + getStringSize("Pathfinder Server Application"));
		
		for(String str : header_poi_tags) {
			new_header_size += getStringSize(str);
		}
		for(String str : header_way_tags) {
			new_header_size += getStringSize(str);
		}
		//System.out.println("new header size: " + new_header_size + ", " + (new_header_size+20));
		
		out.write(magic_bytes);
		writeInt(out, new_header_size);
		writeInt(out, header_file_version);
		long new_file_size = new_header_size + 20;
		for(long l : ret_size) {
			new_file_size += l;
		}
		writeLong(out, new_file_size);
		writeLong(out, System.currentTimeMillis());
		for(int i=0; i<4; i++) {
			writeInt(out, (int)(box[i]*1000000));
		}
		writeShort(out, header_tile_size);
		writeString(out, header_projection);
		
		boolean flag_debug = false, 
				flag_map_start_pos = false, 
				flag_start_zoom = false, 
				flag_lang = false, 
				flag_comment = false, 
				flag_created_by = false;
		if((header_flags&0x80)>0) flag_debug = true;
		if((header_flags&0x40)>0) {
			flag_map_start_pos = true;
		} else {
			header_flags += 64;
			flag_map_start_pos = true;
		}
		if((header_flags&0x20)>0) {
			flag_start_zoom = true;
		} else {
			header_flags += 32;
			flag_start_zoom = true;
		}
		if((header_flags&0x10)>0) flag_lang = true;
		if((header_flags&0x08)>0) flag_comment = true;
		if((header_flags&0x04)>0) flag_created_by = true;
		
		out.write(header_flags);
		
		int middle_lat = (int)(((box[2]-box[0]) * 1000000)/2 + (box[0]*1000000));
		int middle_lon = (int)(((box[3]-box[1]) * 1000000)/2 + (box[1]*1000000));
		writeInt(out, middle_lat);
		writeInt(out, middle_lon);
		
		out.write((byte)start_zoom);
		
		if(flag_lang) {
			writeString(out, header_lang_pref);
		}
		if(flag_comment) {
			writeString(out, header_comment);
		}
		if(flag_created_by) {
			writeString(out, "Pathfinder Server Application");
		}
		writeShort(out, (short)header_poi_tags.length);
		for(int i=0; i<header_poi_tags.length; i++) {
			writeString(out, header_poi_tags[i]);
		}
		writeShort(out, (short)header_way_tags.length);
		for(int i=0; i<header_way_tags.length; i++) {
			writeString(out, header_way_tags[i]);
		}
		
		out.write(header_zoom_intervals);
		long start_pos = new_header_size + 20;
		for(int i=0; i<header_zoom_intervals; i++) {
			out.write(z_intervals[i].base_zoom_level);
			out.write(z_intervals[i].minimal_zoom_level);
			out.write(z_intervals[i].maximal_zoom_level);
			writeLong(out, start_pos);
			writeLong(out, ret_size[i]);
			start_pos += ret_size[i];
		}
		
		// write subfiles
		for(int i=0; i<header_zoom_intervals; i++) {
			long offset = ret_tile_counter[i] * 5;
			byte[] index_entry = new byte[5];
			int[][] tile_interval = getTileInterval(subfiles[i].getBaseZoom(), bb);
			int[] tile_map = getMapSize(subfiles[i].getBaseZoom(), header_bounding_box);
			int[] map_offset = getXYOffset(subfiles[i].getBaseZoom(), header_bounding_box);
			
			in = new FileInputStream(path_to_file);
			in.skip(header_size);
			long subfile_skip = 0;
			for(int j=0; j<i+1; j++) {
				subfile_skip += subfiles[j].getTileCount()*5;
				if(j>0) {
					subfile_skip += subfiles[j-1].getSubfileSize();
					subfile_skip -= subfiles[j-1].getTileCount()*5;
				}
			}
			//System.out.println("subfile_skip: " + subfile_skip + " bytes");
			in.skip(subfiles[i].getTileCount()*5);

			// create the index entries
			for(int j=0; j<tile_map[1]; j++) {
				for(int k=0; k<tile_map[0]; k++) {
					if((k>=tile_interval[0][0]-map_offset[0])
							&&(k<=tile_interval[0][1]-map_offset[0])
							&&(j>=tile_interval[1][0]-map_offset[1])
							&&(j<=tile_interval[1][1]-map_offset[1])) {
						// read tile
						if(ret_covered_by_water.get(i)[j*tile_map[0]+k]) {
							index_entry[0] = (byte)128;
						} else {
							index_entry[0] = (byte)0;
						}
						ByteBuffer b = ByteBuffer.allocate(8);
						b.putLong(offset);
						byte[] l = b.array();
						index_entry[0] = (byte)(l[3]&127);
						index_entry[1] = l[4];
						index_entry[2] = l[5];
						index_entry[3] = l[6];
						index_entry[4] = l[7];
						out.write(index_entry);
						//System.out.println("INDEX: " + toBinaryString(index_entry));
						offset += subfiles[i].getTileSizes()[j*tile_map[0]+k];
					} else {
						// skip tile
						in.skip(subfiles[i].getTileSizes()[j*tile_map[0]+k]);
					}
				}
			}
			in.close();
			
			// copy the tiles
			in = new FileInputStream(path_to_file);
			in.skip(header_size);
			subfile_skip = 0;
			for(int j=0; j<i+1; j++) {
				subfile_skip += subfiles[j].getTileCount()*5;
				if(j>0) {
					subfile_skip += subfiles[j-1].getSubfileSize();
					subfile_skip -= subfiles[j-1].getTileCount()*5;
				}
			}
			in.skip(subfile_skip);
			//System.out.println("subfile_skip2: " + subfile_skip + " bytes");

			for(int j=0; j<tile_map[1]; j++) {
				for(int k=0; k<tile_map[0]; k++) {
					if((k>=tile_interval[0][0]-map_offset[0])
							&&(k<=tile_interval[0][1]-map_offset[0])
							&&(j>=tile_interval[1][0]-map_offset[1])
							&&(j<=tile_interval[1][1]-map_offset[1])) {
						// read tile
						byte[] b = new byte[(int)subfiles[i].getTileSizes()[j*tile_map[0]+k]]; // TODO this might fuck up stuff in the future (long index) casted to int
						in.read(b);
						out.write(b);
					} else {
						// skip tile
						in.skip(subfiles[i].getTileSizes()[j*tile_map[0]+k]);
					}
				}
			}
			
			
		}
		out.close();
		in.close();
		return file_dest;
	}

	private void readHeader(FileInputStream in) throws IOException {
		boolean flag_debug = false, 
				flag_map_start_pos = false, 
				flag_start_zoom = false, 
				flag_lang = false, 
				flag_comment = false, 
				flag_created_by = false;
		byte[] barry = new byte[1];
		int d = 0;
		
		// magic bytes
		for(int i=0; i<20; i++) {
			in.read(barry);
			magic_bytes[i] = barry[0];
		}
		header_size = getInt(in) + 24;
		header_file_version = getInt(in);
		header_file_size = getLong(in);
		header_date_of_creation = getLong(in);
		BoundingBox bb = new BoundingBox(getInt(in), getInt(in), getInt(in), getInt(in));
		header_bounding_box = bb;
		header_tile_size = getShort(in);
		header_projection = getString(in);
		
		// flags
		in.read(barry);
		header_flags = barry[0];
		if((barry[0]&0x80)>0) flag_debug = true;
		if((barry[0]&0x40)>0) flag_map_start_pos = true;
		if((barry[0]&0x20)>0) flag_start_zoom = true;
		if((barry[0]&0x10)>0) flag_lang = true;
		if((barry[0]&0x08)>0) flag_comment = true;
		if((barry[0]&0x04)>0) flag_created_by = true;
		
		// map start position
		if(flag_map_start_pos) {
			header_map_start_pos[0] = getInt(in);
			header_map_start_pos[1] = getInt(in);
		}
		
		// start zoom level
		if(flag_start_zoom) {
			in.read(barry);
			header_start_zoom_level = barry[0];
		}
		
		// language preferences
		if(flag_lang) {
			header_lang_pref = getString(in);
		}

		// comment
		if(flag_comment) {
			header_comment = getString(in);
		}

		// created by
		if(flag_created_by) {
			header_created_by = getString(in);
		}
		
		// poi tags
		short max = getShort(in);
		header_poi_tags = new String[max];
		for(int i=0; i<max; i++) {
			header_poi_tags[i] = getString(in);
		}

		// ways
		max = getShort(in);
		header_way_tags = new String[max];
		for(int i=0; i<max; i++) {
			header_way_tags[i] = getString(in);
		}
		
		// amount of zoom intervals
		in.read(barry);
		header_zoom_intervals = barry[0];
		
		// zoom interval configuration
		z_intervals = new ZoomInterval[header_zoom_intervals];
		for(int i=0; i<header_zoom_intervals; i++) {
			
			in.read(barry);
			byte basez = barry[0];
			in.read(barry);
			byte minz = barry[0];
			in.read(barry);
			byte maxz = barry[0];
			long abs = getLong(in);
			long size = getLong(in);
			z_intervals[i] = new ZoomInterval(basez, minz, maxz, abs, size);
			
		}

	}
	
	private void createSubfiles(FileInputStream in, ZoomInterval[] intv, int[] tile_count) throws IOException {
		for(int i=0; i<intv.length; i++) {
			long[] tiles = new long[tile_count[i]];
			long[] tile_size = new long[tile_count[i]];
			boolean[] covered_by_water = new boolean[tile_count[i]];
			long prev = 0;
			for(int j=0; j<tile_count[i]; j++) {
				getIndexEntry(in);
				tiles[j] =  IndexEntry.offset;
				covered_by_water[j] = IndexEntry.covered_by_water;
				if(j>0) {
					tile_size[j-1] = IndexEntry.offset-prev;
				}
				if(j==(tile_count[i]-1)) {
					tile_size[j] = intv[i].size_subfile - IndexEntry.offset;
				}
				prev = IndexEntry.offset;
			}
			for(int j=0; j<tile_count[i]; j++) {
				//System.out.println("offset: " + tiles[j] + ", size: " + tile_size[j]);
			}
			Subfile s = new Subfile(intv[i], tile_count[i], tiles, tile_size, covered_by_water, this.flag_debug);
			subfiles[i] = s;
			in.skip(intv[i].size_subfile-(tiles.length*5));
			
		}
	}
	
	private short getShort(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[2];
		
		for(int i=0; i<2; i++) {
			in.read(barry);
			buffer[i] = barry[0];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getShort();
	}
	
	private int getInt(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[4];
		
		for(int i=0; i<4; i++) {
			in.read(barry);
			buffer[i] = barry[0];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getInt();
	}
	
	private long getLong(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[8];
		
		for(int i=0; i<8; i++) {
			in.read(barry);
			buffer[i] = barry[0];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getLong();
	}
	
	private String getString(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];

		LEB128UINT string_size = new LEB128UINT(in);
		byte[] tmp_buffer = new byte[string_size.getInteger()];
		for(int i=0; i<string_size.getInteger(); i++) {
			in.read(barry);
			tmp_buffer[i] = barry[0];
		}
		
		return new String(tmp_buffer, "UTF-8");
	}

	public int getTileCount(int base_zoom, BoundingBox bb) {
		// map from 180°W to 180°E, x axis
		// map from 85.0511°N to 85.0511°S, y axis
		// for implementation see https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Java
		int bottom_left_x = (int)Math.floor( (bb.minLon + 180) / 360 * (1<<base_zoom) );
		int bottom_right_x = (int)Math.floor( (bb.maxLon + 180) / 360 * (1<<base_zoom) );
		int map_width = bottom_right_x-bottom_left_x + 1;
		int bottom_left_y = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.minLat)) + 1 / Math.cos(Math.toRadians(bb.minLat))) / Math.PI) / 2 * (1<<base_zoom) ) ;
		int top_left_y = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.maxLat)) + 1 / Math.cos(Math.toRadians(bb.maxLat))) / Math.PI) / 2 * (1<<base_zoom) ) ;
		int map_height = bottom_left_y-top_left_y + 1;
		
		return map_width*map_height;
	}
	
	private int[] getXYOffset(int zoom, BoundingBox bb) {
		int[] ret = new int[2];
		ret[0] = (int)Math.floor( (bb.minLon + 180) / 360 * (1<<zoom));
		ret[1] = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.maxLat)) + 1 / Math.cos(Math.toRadians(bb.maxLat))) / Math.PI) / 2 * (1<<zoom)) ;
		
		return ret;
	}
	
	private int[] getMapSize(int zoom, BoundingBox bb) {
		int[] ret = new int[2];
		int bottom_left_x = (int)Math.floor( (bb.minLon + 180) / 360 * (1<<zoom) );
		int bottom_right_x = (int)Math.floor( (bb.maxLon + 180) / 360 * (1<<zoom) );
		int map_width = bottom_right_x-bottom_left_x + 1;
		ret[0] = map_width;
		int bottom_left_y = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.minLat)) + 1 / Math.cos(Math.toRadians(bb.minLat))) / Math.PI) / 2 * (1<<zoom) ) ;
		int top_left_y = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.maxLat)) + 1 / Math.cos(Math.toRadians(bb.maxLat))) / Math.PI) / 2 * (1<<zoom) ) ;
		int map_height = bottom_left_y-top_left_y + 1;
		ret[1] = map_height;
		
		return ret;
	}
	
	public int[][] getTileInterval(int zoom, BoundingBox bb) {
		int[][] ret = new int[2][2];
		// int[0][0] = x1
		// int[0][1] = x2
		// int[1][0] = y1
		// int[1][1] = y2
		
		ret[0][0] = (int)Math.floor( (bb.minLon + 180) / 360 * (1<<zoom) );
		ret[0][1] = (int)Math.floor( (bb.maxLon + 180) / 360 * (1<<zoom) );
		ret[1][0] = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.maxLat)) + 1 / Math.cos(Math.toRadians(bb.maxLat))) / Math.PI) / 2 * (1<<zoom) ) ;
		ret[1][1] = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(bb.minLat)) + 1 / Math.cos(Math.toRadians(bb.minLat))) / Math.PI) / 2 * (1<<zoom) ) ;
		
		return ret;
	}
	
	private void getIndexEntry(FileInputStream in) throws IOException {
		boolean water = false;
		byte[] barry = new byte[1];
		byte[] buffer = new byte[5+3]; // +3 to stretch it for ByteBuffer.getLong()
		buffer[0] = 0;
		buffer[1] = 0;
		buffer[2] = 0;
		for(int i=3; i<8; i++) {
			in.read(barry);
			buffer[i] = barry[0];
			//writeLog(i);
		}
		if((buffer[3]&0x80)>0) water = true;
		buffer[3] = (byte)(buffer[3]&127);
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		//printByteArray(buffer);
		long l = wrapped.getLong();
		IndexEntry.offset = l;
		IndexEntry.covered_by_water = water;
	}

	private void writeShort(FileOutputStream out, short a) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(2);
		b.putShort(a);
		
		out.write(b.array());
		return;
	}
	
	private void writeInt(FileOutputStream out, int a) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(a);
		
		out.write(b.array());
		return;
	}
	
	private void writeLong(FileOutputStream out, long a) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putLong(a);
		
		out.write(b.array());
		return;
	}
	
	private void writeString(FileOutputStream out, String a) throws UnsupportedEncodingException, IOException {
		LEB128UINT size = new LEB128UINT(a.length());
		out.write(size.getBytes());
		out.write(a.getBytes("UTF-8"));
		return;
	}
	
	private int getStringSize(String str) throws UnsupportedEncodingException {
		LEB128UINT size = new LEB128UINT(str.length());
		
		return size.getBytes().length + str.getBytes("UTF-8").length;
	}
	
}

class IndexEntry{
	public static long offset;
	public static boolean covered_by_water;
}