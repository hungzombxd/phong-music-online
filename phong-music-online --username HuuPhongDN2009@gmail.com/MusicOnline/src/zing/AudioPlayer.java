package zing;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer implements AudioPlayerListener, Streaming{
	private AudioStream in;
	private byte[] buffer = new byte[32768];
	private boolean stoped = false;
	private SourceDataLine source;
	private AudioFormat fmtTarget;
	private boolean paused = false;
	private AudioPlayerListener listener;
	private int reading = -1;
	private AudioDecoder decoder;
	private int plusDuration = 0;
	private Streaming streaming;
	private AudioInfo audioInfo;
	private Object locked = new Object();
	
	public AudioPlayer(){
		listener = this;
		streaming = this;
	}
	
	public void setListener(AudioPlayerListener listener){
		this.listener = listener;
	}
	
	public void setStreaming(Streaming streaming){
		this.streaming = streaming;
	}
	
	private void prepare(String url){
		in = new FileAudioStream(url, streaming);
		switch (in.getType()) {
		case MemoryAudioStream.MP3_STREAM:
			decoder = new MP3FileDecoder(in);
			break;
			
		case MemoryAudioStream.FLAC_STREAM:
			decoder = new FLACFileDecoder(in);
			break;

		default:
			throw new RuntimeException("Not support this file type");
		}
		audioInfo = decoder.getAudioInfo();
		listener.init(this);
	}
	
	public int getBuffering(){
		if (decoder == null) return 0;
		return decoder.sizeToDuration(in.getBufferingValue());
	}
	
	public int getLength(){
		if (in == null) return 0;
		return in.getLength();
	}
	
	public boolean isBuffered(){
		if (in == null) return false;
		return in.isCompleted();
	}
	
	public void seek(int duration){
		if (source == null) return;
		source.stop();
		source.close();
		plusDuration = duration;
		decoder.seek(plusDuration);
		try {
			source.open(fmtTarget);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		source.start();
	}
	
	public void play(String url){
		stop();
		synchronized (locked) {
			prepare(url);
			createSource();
			stoped = false;
			plusDuration = 0;
			while (!stoped && ((reading = decoder.getPCMData(buffer)) != -1)){
				if (paused){
					synchronized (source) {
						source.stop();
						listener.paused(this);
						try {
							source.wait();
							source.start();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if(!stoped) listener.playing(this);
				source.write(buffer, 0, reading);
			}
			if (in != null){
				in.closeStream();
			}
			if(!stoped){
				source.drain();
				listener.playing(this);
			}
			source.flush();
			source.close();
			source = null;
			decoder = null;
			stoped = true;
			listener.finished(this);
		}
		
	}
	
	private void createSource(){
		fmtTarget = decoder.getAudioFormat();
		try {
			Line line = AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, fmtTarget));
			if (line instanceof SourceDataLine) {
				source = (SourceDataLine) line;
				source.open(fmtTarget);
				source.start();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void stop(){
		if (paused) resume();
		stoped = true;
	}
	
	public void pause(){
		paused = true;
	}
	
	public void resume(){
		paused = false;
		synchronized (source) {
			source.notifyAll();
		}
	}
	
	public int getDuration(){
		if (decoder == null) return 0;
		return decoder.getDuration();
	}
	
	public AudioInfo getAudioInfo(){
		if (decoder == null) return new AudioInfo();
		return audioInfo;
	}
	
	public static String toDuaration(int position){
		int min = position / 60000;
		int sec = (position / 1000) % 60;
		return numberToString(min) + ":" + numberToString(sec);
	}

	private static String numberToString(int number){
		String ret = String.valueOf(number);
		while (ret.length() < 2){
			ret = "0" + ret;
		}
		return ret;
	}
	
	public int getCurrentDuration(){
		if (source == null) return 0;
		return (int) (plusDuration + (source.getMicrosecondPosition() / 1000));
	}
	
	public String getPlayingDuration(){
		return toDuaration(getCurrentDuration()) + " / " + toDuaration(getDuration());
	}
	
	public String getPlayingInfo(){
		return "[" + getAudioInfo() + " | " + getPlayingDuration() + "]";
	}
	
	public int durationToSize(int duration){
		if (decoder == null) return 0;
		return decoder.durationToSize(duration);
	}
	
	public int sizeToDuration(int size){
		if (decoder == null) return 0;
		return decoder.sizeToDuration(size);
	}
	
	public void release(){
		if (in != null){
			in.closeStream();
			in.release();
		}
	}
	
	public void playing(AudioPlayer player) {
	}

	public void finished(AudioPlayer player) {
	}

	public void paused(AudioPlayer player) {
	}

	public void buffering(int length) {
	}

	public void init(AudioPlayer player) {
	}
}
