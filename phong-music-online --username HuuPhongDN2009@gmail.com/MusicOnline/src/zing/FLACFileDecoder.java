package zing;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.FrameListener;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.Padding;
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
	private double sampleSize;
	private Padding padding;
	private int audioSize;

	public FLACFileDecoder(AudioStream in) {
		this.in = in;
		decoder = new FLACDecoder(in);
		decoder.addFrameListener(new FrameListener() {
			
			@Override
			public void processMetadata(Metadata metadata) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void processFrame(Frame frame) {
//				System.out.println("==========================================");
//				System.out.println("Sample Origin: " + frame.header.sampleNumber);
//				System.out.println(frame.header.sampleNumber * 1000.0 / streamInfo.getSampleRate());
			}
			
			@Override
			public void processError(String msg) {
				// TODO Auto-generated method stub
				
			}
		});
		try {
			streamInfo = decoder.readStreamInfo();
			//metaDataLength = streamInfo.getLength() + 8;
			Metadata metadata;
	        do {
	        	metadata = decoder.readNextMetadata();
	        	System.out.println(metadata);
	        	//metaDataLength += metadata.getLength() + 4;
	        	if (metadata instanceof SeekTable){
	        		seekTable = (SeekTable) metadata;
	        	} else if (metadata instanceof Padding){
	        		padding = (Padding) metadata;
	        	}
	        } while (!metadata.isLast());
	        metaDataLength = (int) decoder.getTotalBytesRead();
			duration = (int) (streamInfo.getTotalSamples() * 1000.0 / streamInfo.getSampleRate());
			bitrate = (int) (((in.getLength() - metaDataLength) * 8.0) / (streamInfo.getTotalSamples() / streamInfo.getSampleRate()));
			audioInfo = new AudioInfo("FLAC", true, bitrate / 1000, streamInfo.getSampleRate(), streamInfo.getChannels(), in.getLength(), getDuration());
			fmt = streamInfo.getAudioFormat();
			audioSize = in.getLength() - metaDataLength - (int) (in.getLength() - (streamInfo.getTotalSamples() * (streamInfo.getBitsPerSample() / 8)));
			System.out.println("Padding : " + (streamInfo.getTotalSamples() / streamInfo.getMaxBlockSize() * padding.getLength()) + metaDataLength);
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
				pcmData = decoder.decodeFrame(frame, pcmData);
				System.arraycopy(pcmData.getData(), 0, buffer, 0, pcmData.getLen());
			}else{
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pcmData.getLen();
	}

	public int seek(int duration){
		seeking = true;
		int samplesOfTime = (int) ((duration / 1000.0) * streamInfo.getSampleRate());
//		System.out.println(samplesOfTime);
//		System.out.println(samplesOfTime / 2.0 * duration / streamInfo.getTotalSamples());
//		System.out.println(durationToSize(duration));
		decoder.setSamplesDecoded(samplesOfTime);
		in.seek(durationToSize(duration));
		seeking = false;
		synchronized (locked) {
			locked.notifyAll();
		}
		return 0;
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
//		System.out.println("Size: " + bitrate / 8000.0 * duration);
		return (int) (bitrate / 8000.0 * duration);
	}

	public int sizeToDuration(int size) {
		return (int) ((size) * 1.0 / (in.getLength()) * duration);
	}
}
