package zing;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.FrameListener;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.SeekTable;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

public class FLACFileDecoder implements AudioDecoder{
	private FLACDecoder decoder;
	private ByteData pcmData;
	private StreamInfo streamInfo;
	private Metadata[] metaData;
	private AudioFormat fmt;
	private int bitrate;
	private AudioStream in;
	private int duration;
	private AudioInfo audioInfo;
	private int metaDataLength;
	private SeekTable seekTable;
	private Object locked = new Object();
	private Object done = new Object();
	private boolean seeking = false;
	private boolean doneDecodeFrame = false;
	private double sampleSize;

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
	        	if (metadata instanceof SeekTable) seekTable = (SeekTable) metadata;
	        } while (!metadata.isLast());
	        metaDataLength = (int) decoder.getTotalBytesRead();
			duration = (int) (streamInfo.getTotalSamples() / streamInfo.getSampleRate()) * 1000;
			bitrate = (int) (((in.getLength() - metaDataLength) * 8.0) / (streamInfo.getTotalSamples() / streamInfo.getSampleRate()));
			audioInfo = new AudioInfo("FLAC", true, bitrate / 1000, streamInfo.getSampleRate(), streamInfo.getChannels(), in.getLength(), getDuration());
			fmt = streamInfo.getAudioFormat();
			sampleSize = (in.getLength() - metaDataLength) / streamInfo.getTotalSamples();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AudioFormat getAudioFormat() {
		return fmt;
	}

	public int getPCMData(byte[] buffer) {
		if (seeking){
			synchronized (locked) {
				try {
					locked.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		doneDecodeFrame = false;
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
		doneDecodeFrame = true;
		synchronized (done) {
			done.notifyAll();
		}
		return pcmData.getLen();
	}

	public int seek(int time){
		seeking = true;
		if (!doneDecodeFrame){
			synchronized (done) {
				try {
					done.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		int samplesOfTime = time * streamInfo.getSampleRate();
		System.out.println("Sample Size : " + sampleSize);
		System.out.println("First : " + (samplesOfTime * (int)sampleSize) + metaDataLength);
		int count = (int) ((time * bitrate) / 8) + metaDataLength;
		System.out.println("Count : " + count);
		System.out.println("Size : " + in.getLength());
		in.seek((int) ((time * bitrate) / 8) + metaDataLength);
		System.out.println("SampleNumber : " + seekTable.getSeekPoint(1).getSampleNumber());
//		System.out.println(samplesOfTime);
//		int index = (int) (samplesOfTime / seekTable.getSeekPoint(1).getSampleNumber());
//		for (int i = index; i < seekTable.numberOfPoints(); i++){
//			if (samplesOfTime <= seekTable.getSeekPoint(i).getSampleNumber()){
//				System.out.println(seekTable.getSeekPoint(i));
//				in.seek((int) (seekTable.getSeekPoint(i).getStreamOffset() + metaDataLength));
//				break;
//			}
//		}
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
		// TODO Auto-generated method stub
		return 0;
	}

	public int sizeToDuration(int size) {
		// TODO Auto-generated method stub
		return 0;
	}
}
