/**
 * 
 * @author pollux
 *
 */
package map;

import java.io.FileInputStream;
import java.io.IOException;

public class LEB128SINT {
	private int value;
	private byte[] bytes;
	private int byte_length = 0;

	public LEB128SINT(byte[] array) {
		value = convertToJavaInt(array);
		byte_length = array.length;
	}

	public LEB128SINT(int x) {
		//ytes = convertToByteArray(x);
	}

	public LEB128SINT(FileInputStream in) {
		byte[] barry = new byte[1];
		byte[] buffer = new byte[32];
		int len = 0;
		try {
			for (int i = 0; i < buffer.length; i++) {
				in.read(barry);
				buffer[i] = barry[0];
				len++;
				if ((barry[0] & 128) == 0)
					break;
			}
			byte[] ret = new byte[len];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = buffer[i];
			}
			value = convertToJavaInt(ret);
			byte_length = ret.length;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getByteCount() {
		return byte_length;
	}

	public int getInteger() {
		return value;
	}

	public byte[] getBytes() {
		return bytes;
	}

	private int convertToJavaInt(byte[] array) {
		int[] arr = new int[array.length];

		// reverse array in case of wrong order, should be LSB---MSB
		// &0xFF because java byte range is from -128 to 127
		if ((array[0] & 0xFF) < 128) {
			for (int i = 0; i < array.length / 2; i++) {
				byte tmp = array[i];
				array[i] = array[array.length - i - 1];
				array[array.length - i - 1] = tmp;
			}
		}

		// grab each 7-bit value as integer
		for (int i = 0; i < array.length; i++) {
			arr[i] = array[i] & 127;
		}

		// calculate the int value
		int value = 0;
		int exponent = 0;
		boolean is_negative = false;
		String tmp = "";
		int exp_mult = 0;
		for (int i : arr) {
			for (int j = 0; j < 7; j++) {
				value += (int) ((i % 2) * Math.pow(2, exponent));
				tmp += "Value[" + exp_mult + "] = (" + i + "%2)->" + (i % 2) + " * 2^" + exponent + "("
						+ (int) Math.pow(2, exponent) + ") = " + value + "\n";
				if ((i % 2) == 1) {
					is_negative = true;
				} else {
					is_negative = false;
				}
				i = i / 2;
				exponent++;
			}
			exp_mult++;
		}

		if (is_negative) {
			return -(((int) Math.pow(2, (exp_mult * 7 - 1)) * 2) - value);
		} else {
			return value;
		}
	}



}
