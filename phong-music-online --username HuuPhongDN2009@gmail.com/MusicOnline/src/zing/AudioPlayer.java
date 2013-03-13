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
		in = new AudioStream(url, streaming);
		switch (in.getType()) {
		case AudioStream.MP3_STREAM:
			decoder = new MP3FileDecoder(in);
			break;
			
		case AudioStream.FLAC_STREAM:
			decoder = new FLACFileDecoder(in);
			break;

		default:
			throw new RuntimeException("Not support this file type");
		}
		audioInfo = decoder.getAudioInfo();
		listener.init(this);
	}
	
	public int getBuffering(){
		return decoder.sizeToDuration(in.getBufferingValue());
	}
	
	public int getLength(){
		return in.getLength();
	}
	
	public boolean isBuffered(){
		return in.isCompleted();
	}
	
	public void seek(int time){
		if (source == null) return;
//		pause();
		source.flush();
		source.stop();
		source.close();
		time = time / 1000;
		plusDuration = time * 1000;
		decoder.seek(time);
		try {
			source.open(fmtTarget);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		source.start();
//		resume();
	}
	
	public void play(String url){
		stop();
		prepare(url);
		createSource();
		stoped = false;
		plusDuration = 0;
		while (!stoped && (reading = decoder.getPCMData(buffer)) != -1){
			if (paused){
				synchronized (this) {
					source.flush();
					listener.paused(this);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			listener.playing(this);
			source.write(buffer, 0, reading);
		}
		if (in != null) in.closeStream();
		in = null;
		source.drain();
		listener.playing(this);
		decoder = null;
		stoped = true;
		if (source != null){
			source.flush();
			source.close();
			source = null;
		}
		listener.finished(this);
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
		synchronized (this) {
			notifyAll();
		}
	}
	
	public int getDuration(){
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
		return decoder.durationToSize(duration);
	}
	
	public int sizeToDuration(int size){
		return decoder.sizeToDuration(size);
	}
	
	public static void main(String[] args) {
//		new AudioPlayer().play("file:D:\\Music\\Lam Hung\\04 - Moi Nguoi Mot Qua Khu.flac");
//		new AudioPlayer().play("file:D:\\Music\\Lam Hung\\Lam Hung - Ky Tuc Xa Chieu Mua.Ape");
//		new AudioPlayer().play("file:D:\\Download\\huuphongdn2009\\Pham Truong - Het.mp3");
		new AudioPlayer().play("http://stream2.mp3.zdn.vn/fsfsdfdsfdserwrwq3/5e031d88f409f0e3ebc923c6d2d66466/51407f2f/2012/02/07/a/2/a2a1f0bc045ed2efd4dfe6bc0d11a534.mp3");
		
	}

	public void playing(AudioPlayer player) {
		System.out.println(getAudioInfo() + " | " + getPlayingDuration());
//		int current = getCurrentDuration();
//		if (current >= 10000 && current < 11000){
//			System.out.println(source.getLongFramePosition());
//			System.out.println(current);
//			seek(200000);
//		}
	}

	public void finished(AudioPlayer player) {
		System.out.println("FINISH");
	}

	public void paused(AudioPlayer player) {
	}

	public void buffering(int length) {
		System.out.println((length * 100.0 / getLength()));
	}

	public void init(AudioPlayer player) {
		
	}
}
