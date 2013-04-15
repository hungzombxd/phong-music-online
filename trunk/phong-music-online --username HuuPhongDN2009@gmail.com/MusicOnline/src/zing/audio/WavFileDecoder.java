package zing.audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class WavFileDecoder implements AudioDecoder {
	
	private AudioStream in;
	private AudioFormat fmt;
	private AudioInfo info;
	private int duration = 0;
	private AudioInputStream stream;
	private Object locked = new Object();
	private boolean seeking = false;
	
	public WavFileDecoder(AudioStream in){
		this.in = in;
		try {
			stream = AudioSystem.getAudioInputStream(in);
			fmt = stream.getFormat();
			duration = (int) (in.length / (fmt.getChannels() * fmt.getSampleRate() / 8000.0 * fmt.getSampleSizeInBits()));
			info = new AudioInfo("WAV", false, (int)(fmt.getChannels() * fmt.getSampleRate() / 1000.0 * fmt.getSampleSizeInBits()), (int) fmt.getSampleRate(), fmt.getChannels(), in.getLength(), duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public AudioFormat getAudioFormat() {
		return fmt;
	}

	@Override
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
		int reading = -1;
		try{
			reading = stream.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reading;
	}

	@Override
	public boolean seekable() {
		return true;
	}

	@Override
	public void seek(int size) {
		seeking = true;
		in.seek(size);
		seeking = false;
		synchronized (locked) {
			locked.notifyAll();
		}
	}

	@Override
	public AudioInfo getAudioInfo() {
		return info;
	}

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public int durationToSize(int duration) {
		return 0;
	}

	@Override
	public int sizeToDuration(int size) {
		return (int) (size * 1.0 / in.length * duration);
	}
}
