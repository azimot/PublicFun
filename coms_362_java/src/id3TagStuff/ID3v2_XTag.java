package id3TagStuff;

import id3TagStuff.frames.ID3v2_XFrame;
import id3TagStuff.frames.ID3v2_XFrameFactory;
import id3TagStuff.frames.v2.ID3v2_2Frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import util.Util;

public class ID3v2_XTag {

	private File mp3File;
	private ID3v2_XTagHeader header;
	private List<ID3v2_XFrame> frames;

	public ID3v2_XTag(File mp3FileP) throws IOException {
		mp3File = mp3FileP;
		InputStream in = new FileInputStream(mp3File);
		byte[] headerBytes = new byte[10];
		in.read(headerBytes);
		header = new ID3v2_XTagHeader(Util.castByteArrToIntArr(headerBytes));
		frames = new ArrayList<ID3v2_XFrame>();
		int bytesLeft = header.getSize();
		int frameHeadderLength = header.getMajorVersion() == 2? 6:10;
		byte[] frameHeadderBytes = new byte[frameHeadderLength];
		while (bytesLeft > 0) {
			in.read(frameHeadderBytes);
			// TODO framefactory to create the necessary v2_2 or v2_3
			ID3v2_XFrame frame = ID3v2_XFrameFactory.getFrame(Util
					.castByteArrToIntArr(frameHeadderBytes), in);
			frames.add(frame);
			bytesLeft -= frameHeadderLength;
			bytesLeft -= frame.getSize();
			System.out.println(bytesLeft);
		}
	}

	@Override
	public String toString() {
		return String.format("File name: %s, Header data: %s", mp3File.getName(), header
				.toString());
	}

	public List<ID3v2_XFrame> getAllFrames() {
		return frames;
	}
}
