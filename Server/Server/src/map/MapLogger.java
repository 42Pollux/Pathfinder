/**
 * 
 * @author pollux
 *
 */
package map;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.Month;

public class MapLogger {
	private static FileOutputStream out_f;
	private static FileInputStream in_f;
	private static PrintWriter pw;
	private static long bytes_read;
	private static int  tmd = 0;

	public MapLogger() {
		// TODO Auto-generated constructor stub
	}
	
	public static String readFile(String path) {
		File f = new File(path);
		String header = "";
		try {
			createFileLog(f);
		} catch (Exception e) {
			pw.close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return header;
	}
	
	public static void createFileLog(File f) throws IOException {
		boolean flag_debug = false, 
				flag_map_start_pos = false, 
				flag_start_zoom = false, 
				flag_lang = false, 
				flag_comment = false, 
				flag_created_by = false;
		byte[] magic_bytes = new byte[20];
		byte[] barry = new byte[1];
		bytes_read = 0;
		in_f = new FileInputStream(f);
		out_f = new FileOutputStream("/home/pollux/Desktop/map_reader_log2.log");
		pw = new PrintWriter(out_f);
		String ret = "";
		
		// magic bytes
		for(int i=0; i<20; i++) {
			bytes_read += in_f.read(barry);
			magic_bytes[i] = barry[0];
		}
		writeLog( "magic_bytes: " + (new String(magic_bytes, "UTF-8") + "\n"));
		
		// header size
		writeLog( "header_size: " + getInt(in_f) + "\n");
		
		// file version
		writeLog( "file_version: " + getInt(in_f) + "\n");
		
		// file size
		writeLog( "file_size: " + getLong(in_f) + "\n");
		
		// date of creation
		LocalDate local = LocalDate.of(1970, Month.JANUARY, 1);
		local = local.plusDays(getLong(in_f)/1000/60/60/24);
		writeLog( "date_of_creation: " + local.toString() + "\n");
		
		// bounding box
		BoundingBox bb = new BoundingBox(getInt(in_f), getInt(in_f), getInt(in_f), getInt(in_f));
		writeLog( "minLat: " + bb.minLat + "\n");
		writeLog( "minLon: " + bb.minLon + "\n");
		writeLog( "maxLat: " + bb.maxLat + "\n");
		writeLog( "maxLon: " + bb.maxLon + "\n");
		
		// tile size in pixel
		writeLog( "tile_size: " + getShort(in_f) + "px\n");
		
		// projection string
		writeLog( "projection_string: " + getString(in_f) + "\n");
		
		// flags
		bytes_read += in_f.read(barry);
		if((barry[0]&0x80)>0) flag_debug = true;
		if((barry[0]&0x40)>0) flag_map_start_pos = true;
		if((barry[0]&0x20)>0) flag_start_zoom = true;
		if((barry[0]&0x10)>0) flag_lang = true;
		if((barry[0]&0x08)>0) flag_comment = true;
		if((barry[0]&0x04)>0) flag_created_by = true;
//		writeLog("barry: " + (barry[0]&0x80) + "" + (barry[0]&0x40) + "" + (barry[0]&0x20) + "" + (barry[0]&0x10) + "" + (barry[0]&0x08) + "" + (barry[0]&0x04));
		
		// map start position
		if(flag_map_start_pos) {
			writeLog("map_start_pos_lat: " + getInt(in_f) + "\n");
			writeLog("map_start_pos_lon: " + getInt(in_f) + "\n");
		}
		
		// start zoom level
		if(flag_start_zoom) {
			bytes_read += in_f.read(barry);
			writeLog("start_zoom_level: " + barry[0] + "\n");
		}
		
		// language preferences
		if(flag_lang) {
			writeLog("language_preferences: " + getString(in_f) + "\n");
		}
		
		// comment
		if(flag_comment) {
			writeLog("comment: " + getString(in_f) + "\n");
		}
		
		// created by
		if(flag_created_by) {
			writeLog("created_by: " + getString(in_f) + "\n");
		}
		
		// poi tags
		short max = getShort(in_f);
		writeLog("number_of_poi_tags: " + max + "\n");
		for(int i=0; i<max; i++) {
			writeLog("poi_tag: [" + i + "] - " + getString(in_f) + "\n");
		}
		
		// ways
		max = getShort(in_f);
		writeLog("number_of_way_tags: " + max + "\n");
		for(int i=0; i<max; i++) {
			writeLog("way_tag: [" + i + "] - " + getString(in_f) + "\n");
		}
		
		// amount of zoom intervals
		int zooms = 0;
		bytes_read += in_f.read(barry);
		zooms = barry[0];
		writeLog("zoom_intervals: " + zooms + "\n");
		int[] zoom_level = new int[zooms];
		int[] zoom_level_diff = new int[zooms];
		
		// zoom interval configuration
		for(int i=0; i<zooms; i++) {
			int min_z = 0;
			int max_z = 0;
			writeLog("zoom interval " + i + "\n");
			bytes_read += in_f.read(barry);
			writeLog("--base_zoom_level: " + barry[0] + "\n");
			zoom_level[i] = barry[0];
			bytes_read += in_f.read(barry);
			writeLog("--minimal_zoom_level: " + barry[0] + "\n");
			min_z = (int)barry[0];
			bytes_read += in_f.read(barry);
			writeLog("--maximal_zoom_level: " + barry[0] + "\n");
			max_z = (int)barry[0];
			zoom_level_diff[i] = max_z - min_z + 1;
			writeLog("--zoom_level_diff: " + zoom_level_diff[i] + "\n");
			writeLog("--absolute_start_pos_subfile: " + getLong(in_f) + "\n");
			writeLog("--size_of_subfile: " + getLong(in_f) + "\n");
			
		}
		
		writeLog( "bytes read so far: " + bytes_read + "\n\n");
		
		// for each sub file
		for(int i=0; i<zooms; i++) {
			System.out.println("Reading sub file " + i + "\n");
			writeLog("---------- SUBFILE " + i + " ----------\n");
			
			// index header
			if(flag_debug) {
				byte[] tmp = new byte[16];
				for(int j=0; j<16; j++) {
					bytes_read += in_f.read(barry);
					tmp[j] = barry[0];
				}
				writeLog( "index_header: " + (new String(tmp, "UTF-8") + "\n"));
			}
			
			//index entries
			int entries = getTileCount(zoom_level[i], bb);
			for(int j=0; j<entries; j++) {
				writeLog("tile_entry_offset: [" + j + "] - " + getIndexEntry(in_f) + "\n");
			}
			
			// for each tile
			for(int j=0; j<entries; j++) {
				System.out.println("--Reading tile " + (j+1) + " of " + entries);
				int pois = 0, ways = 0;
				// contains '###TileStartX,Y###' padded to 32 bytes
				if(flag_debug) {
					byte[] tmp = new byte[32];
					for(int k=0; k<32; k++) {
						bytes_read += in_f.read(barry);
						tmp[k] = barry[0];
					}
					writeLog( "tile_header: " + (new String(tmp, "UTF-8") + "\n"));
				}
				
				// zoom table
				for(int k=0; k<zoom_level_diff[i]; k++) {
					int poi = 0, way = 0;
					LEB128UINT col1 = new LEB128UINT(in_f);
					LEB128UINT col2 = new LEB128UINT(in_f);
					bytes_read += col1.getByteCount();
					bytes_read += col2.getByteCount();
					poi = col1.getInteger();
					way = col2.getInteger();
					pois += poi;
					ways += way;
					writeLog( "row " + k + ", col 1: " + poi + "\n");
					writeLog( "row " + k + ", col 2: " + way + "\n");
				}
				
				// first way offset
				LEB128UINT way_offset = new LEB128UINT(in_f);
				bytes_read += way_offset.getByteCount();
				writeLog( "first_way_offset: " + way_offset.getInteger() + "\n");
				
				// for each poi -> poi data
				for(int l = 0; l<pois; l++) {
					// ***POIStartX*** padded to 32 bytes
					if(flag_debug) {
						byte[] tmp = new byte[32];
						for(int k=0; k<32; k++) {
							bytes_read += in_f.read(barry);
							tmp[k] = barry[0];
						}
						writeLog( "poi_header: " + (new String(tmp, "UTF-8") + "\n"));
					}
					
					// geo coordinate difference to the top-left corner of the current tile as VBE-S INT, in the order lat-diff, lon-diff
					LEB128SINT position_lat_diff = new LEB128SINT(in_f);
					LEB128SINT position_lon_diff = new LEB128SINT(in_f);
					bytes_read += position_lat_diff.getByteCount();
					bytes_read += position_lon_diff.getByteCount();
					writeLog( "--(" + l + ") lat_diff " + position_lat_diff.getInteger() + "\n");
					writeLog( "--(" + l + ") lon_diff " + position_lon_diff.getInteger() + "\n");
					
					// special byte
					bytes_read += in_f.read(barry);
					byte special_byte = barry[0];
					int number_of_poi_tags = special_byte&15;
					writeLog( "--(" + l + ")number_of_poi_tags: " + number_of_poi_tags + "\n");
					
					// for each poi tag
					for(int m=0; m<number_of_poi_tags; m++) {
						LEB128UINT tag_id = new LEB128UINT(in_f);
						bytes_read += tag_id.getByteCount();
						writeLog( "  -poi_tag_id: " + tag_id.getInteger() + "\n");
					}
					
					// poi flags
					bytes_read += in_f.read(barry);
					boolean flag_has_name = false;
					boolean flag_has_number = false;
					boolean flag_has_elevation = false;
					if((barry[0]&128)>0) flag_has_name = true;
					if((barry[0]&64)>0) flag_has_number = true;
					if((barry[0]&32)>0) flag_has_elevation = true;
					
					// flag values
					if(flag_has_name) {
						writeLog( "--(" + l + ") poi name=" + getString(in_f) + "\n");
					}
					if(flag_has_number) {
						writeLog( "--(" + l + ") poi house_number=" + getString(in_f) + "\n");
					}
					if(flag_has_elevation) {
						LEB128SINT elev = new LEB128SINT(in_f);
						bytes_read += elev.getByteCount();
						writeLog( "--(" + l + ") poi elevation=" + elev.getInteger() + "\n");
					}
					
					writeLog("\n");
				}
				
				// for each way -> way properties/data
				for(int l=0; l<ways; l++) {
					// ***WayStartX*** padded to 32 bytes
					if(flag_debug) {
						byte[] tmp = new byte[32];
						for(int k=0; k<32; k++) {
							bytes_read += in_f.read(barry);
							tmp[k] = barry[0];
						}
						writeLog("poi_header: " + (new String(tmp, "UTF-8") + "\n"));
					}
					
					// way data size
					LEB128UINT way_data_size = new LEB128UINT(in_f);
					bytes_read += way_data_size.getByteCount();
					
					// sub tile bitmap
					byte[] sub_tile_bitmap = new byte[2];
					bytes_read += in_f.read(barry);
					sub_tile_bitmap[0] = barry[0]; 
					bytes_read += in_f.read(barry);
					sub_tile_bitmap[1] = barry[0];
					
					// special byte
					bytes_read += in_f.read(barry);
					byte special_byte = barry[0];
					int number_of_way_tags = special_byte&15;
					writeLog( "--(" + l + ")number_of_way_tags: " + number_of_way_tags + "\n");
					
					// for each way tag
					for(int m=0; m<number_of_way_tags; m++) {
						LEB128UINT tag_id = new LEB128UINT(in_f);
						bytes_read += tag_id.getByteCount();
						writeLog( "  -way_tag_id: " + tag_id.getInteger() + "\n");
					}
					
					// way flags
					bytes_read += in_f.read(barry);
					boolean flag_has_name = false;
					boolean flag_has_number = false;
					boolean flag_has_reference = false;
					boolean flag_has_label_position = false;
					boolean flag_has_wdb = false;
					boolean flag_wcb_encoding = false;
					if((barry[0]&128)>0) flag_has_name = true;
					if((barry[0]&64)>0) flag_has_number = true;
					if((barry[0]&32)>0) flag_has_reference = true;
					if((barry[0]&16)>0) flag_has_label_position = true;
					if((barry[0]&8)>0) flag_has_wdb = true;
					if((barry[0]&4)>0) flag_wcb_encoding = true;
					
					// flag values
					if(flag_has_name) {
						writeLog( "--(" + l + ") way name=" + getString(in_f) + "\n");
					}
					if(flag_has_number) {
						writeLog( "--(" + l + ") way number=" + getString(in_f) + "\n");
					}
					if(flag_has_reference) {
						writeLog( "--(" + l + ") way ref=" + getString(in_f) + "\n");
					}
					if(flag_has_label_position) {
						LEB128SINT position_lat_diff = new LEB128SINT(in_f);
						LEB128SINT position_lon_diff = new LEB128SINT(in_f);
						bytes_read += position_lat_diff.getByteCount();
						bytes_read += position_lon_diff.getByteCount();
						writeLog( "--(" + l + ") way lat_diff " + position_lat_diff.getInteger() + "\n");
						writeLog( "--(" + l + ") way lon_diff " + position_lon_diff.getInteger() + "\n");
					}
					
					
					
					// amount of way data blocks
					int way_data_blocks = 1;
					if(flag_has_wdb) {
						LEB128UINT wdb = new LEB128UINT(in_f);
						bytes_read += wdb.getByteCount();
						way_data_blocks = wdb.getInteger();
					}
					
					// for each way data block
					for(int m=0; m<way_data_blocks; m++) {
						
						// number of way coordinate blocks
						LEB128UINT wcbs = new LEB128UINT(in_f);
						bytes_read += wcbs.getByteCount();
						int way_coordinate_blocks = wcbs.getInteger();
						
						//for each way coordinate block
						for(int n=0; n<way_coordinate_blocks; n++) {
							
							// amount of way nodes
							LEB128UINT wn = new LEB128UINT(in_f);
							bytes_read += wn.getByteCount();
							int way_nodes = wn.getInteger();
							
							// geo coordinate difference to the top-left corner of the current tile
							LEB128SINT position_lat_diff = new LEB128SINT(in_f);
							LEB128SINT position_lon_diff = new LEB128SINT(in_f);
							bytes_read += position_lat_diff.getByteCount();
							bytes_read += position_lon_diff.getByteCount();
							writeLog( "--(" + l + ") nodes: " + way_nodes + ", lat_diff: " + position_lat_diff.getInteger() + ", lon_diff: "+ position_lon_diff.getInteger() + "\n");
							
							// geo coordinates of the remaining way nodes stored as differences to the previous way node in microdegrees
							for(int o=0; o<way_nodes-1; o++) {
								LEB128SINT lat_diff = new LEB128SINT(in_f);
								LEB128SINT lon_diff = new LEB128SINT(in_f);
								bytes_read += lat_diff.getByteCount();
								bytes_read += lon_diff.getByteCount();
							}
							
							
						}
					}
					
				}
				
				
			}
			
			
		}
		writeLog( "bytes read so far: " + bytes_read + "\n");
		
		in_f.close();
		pw.close();
		return;
	}
	
	
	
	
	
	
	
	
	private static short getShort(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[2];
		
		for(int i=0; i<2; i++) {
			bytes_read += in.read(barry);
			buffer[i] = barry[0];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getShort();
	}
	
	private static int getInt(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[4];
		
		for(int i=0; i<4; i++) {
			bytes_read += in.read(barry);
			buffer[i] = barry[0];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getInt();
	}
	
	private static long getLong(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[8];
		
		for(int i=0; i<8; i++) {
			bytes_read += in.read(barry);
			buffer[i] = barry[0];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getLong();
	}
	
	private static long getIndexEntry(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[5+3]; // +3 to stretch it for ByteBuffer.getLong()
		buffer[0] = 0;
		buffer[1] = 0;
		buffer[2] = 0;
		for(int i=3; i<8; i++) {
			bytes_read += in.read(barry);
			buffer[i] = barry[0];
			//writeLog(i);
		}
		if((buffer[3]&0x80)>0) ;//fully covered in water
		buffer[3] = (byte)(buffer[3]&127);
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		//printByteArray(buffer);
		long l = wrapped.getLong();
		return l;
	}
	
	private static String getString(FileInputStream in) throws IOException {
		byte[] barry = new byte[1];

		LEB128UINT string_size = new LEB128UINT(in);
		bytes_read += string_size.getBytes().length;
		byte[] tmp_buffer = new byte[string_size.getInteger()];
		for(int i=0; i<string_size.getInteger(); i++) {
			bytes_read += in.read(barry);
			tmp_buffer[i] = barry[0];
		}
		
		return new String(tmp_buffer, "UTF-8");
	}
	
	public static int getTileCount(int base_zoom, BoundingBox bb) {
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
	
	private static void printByteArray(byte[] a) {
		String ret = "";
		
		for(int i=0; i<a.length; i++) {
			for(int j=7; j>=0; j--) {
				if((a[i]&((int)Math.pow(2, j)))>0) {
					ret+="1";
				} else {
					ret+="0";
				}
			}
			ret+=" ";
		}
		System.out.println(ret);
	}
	
	
	private static void writeLog(String txt) {
		pw.write(txt);
	}
	

}
