package UploadHelper;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.io.IOException;

public class Search {
	/**
		 * 
		 */
	private static final long serialVersionUID = 6884793567825243837L;

	public static ArrayList<ArrayList<String>> searchString(String fileName, String phrase, String RteName)
			throws IOException {

		Scanner fileScanner = new Scanner(new File(fileName));

		ArrayList<String> rte = new ArrayList<String>();
		Pattern pattern = Pattern.compile(phrase);
		Matcher matcher = null;
		String found = "";
		char c = fileScanner.next().charAt(0);
		int i = (int) c;

		main: while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			matcher = pattern.matcher(line);

			if (matcher.find()) {
				String nextLine = fileScanner.nextLine();
				String secondNextLine = fileScanner.nextLine();
				rte.add(secondNextLine + " " + line + " " + nextLine);

				/*
				 * do { c = fileScanner.next().charAt(0); found = found + c; // if
				 * (found.length() > 30) { // continue main; // } rte.add(line);
				 */
			} // while (i != 32);
			//// * else if (i == 62) {
			//
			// do {
			// found = found + c;
			// rte.add(found);
			//
			//// } while (i != 62);
			//// }
			////
			//// }
		}
		fileScanner.close();

		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
//		int j = 0;
		rte.stream().forEach(line -> {
			ArrayList<String> lineList = new ArrayList<String>();
			String spliter = String.valueOf('"');
			String[] preSep = line.split(spliter);
			String ele = "0";
//			int l = +1;
			String name = RteName; // + String.valueOf(l);
			// if(preSep.length == 1)
			// return;
			// else
			// {
			lineList.add(preSep[1]);
			lineList.add(preSep[3]);
			lineList.add(ele);
			lineList.add(name);

			// String[] elevArray =preSep[preSep.length-1].split("<ele>");
			// String elevation = elevArray[elevArray.length-1].split("</ele>")[0];
			// lineList.add(elevation);

			// String[] nameArray =preSep[0].split("<name>");
			// String name = nameArray[nameArray.length-1].split("</name>")[0];
			// lineList.add(name);
			temp.add(lineList);
			// }

		});

		return temp;

	}
}
