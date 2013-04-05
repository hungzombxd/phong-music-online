package zing;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.SeekTable;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

public class FLACFileDecoder implements AudioDecoder{
	private FLACDecoder decoder;
	private ByteData pcmData;
	private StreamInfo streamInfo;
	private AudioFormat fmt;
	private int bitrate;
	private AudioStream in;
	private int duration;
	private AudioInfo audioInfo;
	private int metaDataLength;
	private SeekTable seekTable;
	private Object locked = new Object();
	private boolean seeking = false;

	public FLACFileDecoder(AudioStream in) {
		this.in = in;
		decoder = new FLACDecoder(in);
		try {
			streamInfo = decoder.readStreamInfo();
			//metaDataLength = streamInfo.getLength() + 8;
			Metadata metadata;
	        do {
	        	metadata = decoder.readNextMetadata();
	        	//metaDataLength += metadata.getLength() + 4;
	        	if (metadata instanceof SeekTable){
	        		seekTable = (SeekTable) metadata;
	        	}
	        } while (!metadata.isLast());
	        metaDataLength = (int) decoder.getTotalBytesRead();
			duration = (int) (streamInfo.getTotalSamples() * 1000.0 / streamInfo.getSampleRate());
			bitrate = (int) (((in.getLength() - metaDataLength) * 8.0) / (streamInfo.getTotalSamples() / streamInfo.getSampleRate()));
			audioInfo = new AudioInfo("FLAC", true, bitrate / 1000, streamInfo.getSampleRate(), streamInfo.getChannels(), in.getLength(), getDuration());
			fmt = new AudioFormat(streamInfo.getSampleRate(), 16, streamInfo.getChannels(), true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AudioFormat getAudioFormat() {
		return fmt;
	}

	public synchronized int getPCMData(byte[] buffer) {
		if (seeking){
			synchronized (locked) {
				try {
					locked.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			Frame frame = decoder.readNextFrame();
			if (frame != null) {
				pcmData = decoder.decodeFrame(frame, null);
				System.arraycopy(pcmData.getData(), 0, buffer, 0, pcmData.getLen());
			}else{
				return -1;
			}
		} catch (IOException e) {
			return 0;
		}
		return pcmData.getLen();
	}

	public void seek(int size){
		seeking = true;
		in.seek(size);
		seeking = false;
		synchronized (locked) {
			locked.notifyAll();
		}
	}

	public AudioInfo getAudioInfo() {
		return audioInfo;
	}

	public int getDuration() {
		return duration;
	}

	public boolean seekable() {
		return seekTable != null;
	}

	public int durationToSize(int duration) {
		return (int) (bitrate / 8000.0 * duration);
	}

	public int sizeToDuration(int size) {
		size -= metaDataLength;
		if (size < 0) size = 0;
		return (int) ((size) * 1.0 / (in.getLength() - metaDataLength) * duration);
	}
}
