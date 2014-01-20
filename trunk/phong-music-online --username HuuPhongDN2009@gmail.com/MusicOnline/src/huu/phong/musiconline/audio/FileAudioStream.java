package huu.phong.musiconline.audio;

import huu.phong.musiconline.model.Song;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileAudioStream extends AudioStream {
	
	public static final String FILE_REGEX = "file:";
	
	private RandomAccessFile in;
	
	public FileAudioStream(Song song) {
		try {
			in = new RandomAccessFile(song.getDirectLink().substring(FILE_REGEX.length()), "r");
			length = (int) in.length();
			offset = length;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read() throws IOException {
		currentPosition++;
		return in.read();
	}

	@Override
	public void seek(int bytes) {
		if (bytes > length) bytes = length;
		try {
			in.seek(bytes);
			currentPosition = bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void closeStream() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void mark(int readlimit) {
		limit = readlimit;
		markPosition = currentPosition;
	}

	@Override
	public synchronized void reset() throws IOException {
		if ((currentPosition - markPosition) >= limit){
			currentPosition = currentPosition - limit;
		}else{
			currentPosition = markPosition;
		}
		in.seek(currentPosition);
	}

	@Override
	public void release() {
	}

}
