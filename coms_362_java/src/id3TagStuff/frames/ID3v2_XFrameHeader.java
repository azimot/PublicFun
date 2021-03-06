package id3TagStuff.frames;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.Util;



public class ID3v2_XFrameHeader {
	private static final String Tag_ID_FileFolder = "FrameID_Files/";
	private static final String TRANSLATOR_FILE_LOCATION = Tag_ID_FileFolder + "tagToEnglish";
	private static final String ID_CHANGE_FILE_LOCATION = Tag_ID_FileFolder + "v2IDtov3TD";
	private String tagID;
	private int size;
	private int flags;
	private int majorVersion;

	public ID3v2_XFrameHeader(int[] headerBytes) {
		if (headerBytes.length == 6) {
			majorVersion = 2;
			tagID = new String(Util.castIntArrToByteArr(Arrays.copyOfRange(headerBytes, 0, 3)));
			size = (headerBytes[3] << 16) + (headerBytes[4] << 8) + headerBytes[5];
			if (size < 0) {
				size = size & 0xffff;
			}
			flags = 0;
		} else if (headerBytes.length == 10) {
			majorVersion = 3;
			tagID = new String(Util.castIntArrToByteArr(Arrays.copyOfRange(headerBytes, 0, 4)));
			size = (headerBytes[4] << 24) + (headerBytes[5] << 16) + (headerBytes[6] << 8)
					+ headerBytes[7];
			if (size < 0) {
				size = size & 0xffff;
			}
			flags = (headerBytes[8] << 8) + headerBytes[9];
		} else {
			throw new IllegalArgumentException("Header bytes must 6 or 10");
		}
	}

	public ID3v2_XFrameHeader(int version) {
		majorVersion = version;
		flags = 0;
		tagID = version < 3 ? "XXX" : "XXXX";
	}

	public int getSize() {
		return size;
	}

	public String getID() {
		return tagID;
	}

	public int getFlags() {
		return flags;
	}

	public int getVersion() {
		return majorVersion;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setTagID(String ID) {
		tagID = ID;
	}

	public void setFlags() {
		flags = 0;
	}

	public void setVersion(int version) {
		majorVersion = version;
	}

	public static String translateFrameHeaderStringToEnglish(String headerType) {
		Scanner fin = null;
		try {
			fin = new Scanner(new File(TRANSLATOR_FILE_LOCATION));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not find translator file");
			return null;
		}
		while (fin.hasNextLine()) {
			String line = fin.nextLine().trim();
			// skip comments and blank lines
			if (line.startsWith("//") || line.length() == 0) {
				continue;
			}
			// skip malformed lines
			if (!line.contains("=") || line.indexOf('=') != line.lastIndexOf('=')) {
				if (Util.DEBUG) {
					System.out.println("Skipped the line: " + line);
				}
				continue;
			}
			String[] lineSplit = line.split("=");
			String[] tags = lineSplit[0].trim().split(",");
			String desc = lineSplit[1].trim();
			for (int i = 0; i < tags.length; i++) {
				if (tags[i].trim().equals(headerType.trim()))
					return desc;
			}
		}
		if (Util.DEBUG)
			System.out.println("The unknown tag " + headerType + " was found.");
		return "Unknown Tag";
	}

	public static String translate3ByteTagTo4ByteTagAndBack(String id) {
		if (id.length() != 3 || id.length() != 4)
			throw new IllegalArgumentException("Frame ID's must be 3 or 4 bytes long.");
		Scanner fin;
		try {
			fin = new Scanner(new File(ID_CHANGE_FILE_LOCATION));
		} catch (FileNotFoundException e) {
			if (Util.DEBUG) {
				e.printStackTrace();
			}
			return id;
		}
		while (fin.hasNextLine()) {
			String[] ids = fin.nextLine().split(",");
			if (ids.length == 1 && ids[0].trim().equals(id)) {
				throw new NoSuchElementException("The tag " + id + " cannot be translated");
			}
			if (ids[0].trim().equals(id))
				return ids[1].trim();
			if (ids[1].trim().equals(id))
				return ids[0].trim();
		}
		throw new NoSuchElementException("The tag " + id + " is nt supported.");
	}
}
