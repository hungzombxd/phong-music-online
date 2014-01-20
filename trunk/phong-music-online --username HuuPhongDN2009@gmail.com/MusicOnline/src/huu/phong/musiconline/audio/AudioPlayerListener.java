package huu.phong.musiconline.audio;

public interface AudioPlayerListener {
	
	public void playing(AudioPlayer player);
	
	public void finished(AudioPlayer player);
	
	public void paused(AudioPlayer player);
	
	public void init(AudioPlayer player);
	
	public void error(String error);
}
