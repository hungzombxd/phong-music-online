package zing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import javazoom.jl.decoder.JavaLayerException;


public class MediaPlayer extends Thread{
	//VSBPlayer player;
	private Thread thread;
	private Main main;
	private String playingSong;
	private boolean toPlay = false;
    Song currentSong;
    private Configure configure;
    private int duration = 0;
    int actualDuration = 0;
    int plusDuration = 0;
    boolean dragged = false;
    boolean buffered = false;
    private Random random;
    private int index;
    
	public MediaPlayer(Main main, Configure configure) {
		this.main = main;
		this.configure = configure;
		this.start();
		random = new Random();
		init();
		//player = new VSBPlayer();
//		player.addVSBPlayerListener(new VSBPlayerListener() {
//			
//			public void playing(VSBPlayer player) {
//				MediaPlayer.this.main.mediaPlay.setIcon(MediaPlayer.this.main.PAUSED);
//			}
//			
//			public void paused(VSBPlayer player) {
//				MediaPlayer.this.main.mediaPlay.setIcon(MediaPlayer.this.main.PLAY);
//			}
//			
//			public void finished(VSBPlayer player) {
//				MediaPlayer.this.main.mediaPlay.setIcon(MediaPlayer.this.main.PLAY);
//			}
//
//			public void buffering(VSBPlayer player) {
//				
//			}
//
//			public void init(VSBPlayer player) {
//				
//			}
//		});
	}
	
	public void play(int indexSong) throws MalformedURLException, JavaLayerException, InterruptedException{
		toPlay = false;
		//player.stop();
		currentSong = configure.songs.get(indexSong);
		playingSong = currentSong.getTitle();
		main.setTitle("[GETING] [" + playingSong + "] [" + configure.title + "]");
		main.setIconStatus(true);
		main.songInfo.setText("[GETING] [" + playingSong + "] [" + configure.title + "]");
        main.sendToYM(currentSong.getTitle(), currentSong.getLink());
        main.setIconStatus(false);
        main.setSongInfo(currentSong.songInfo);
        toPlay = true;
        index = indexSong;
        synchronized (thread) {
        	thread.notifyAll();
		}
	}
	
	public void init() {
		thread = new Thread() {
			public void run() {
				while (true) {
					if (toPlay) {
						try {
							//player.play(currentSong.getOriginLink());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						synchronized (thread) {
							try {
								thread.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		thread.start();
	}
	
	public String toDuaration(int position){
		int min = position / 60000;
		int sec = (position / 1000) % 60;
		return numberToString(min) + ":" + numberToString(sec);
	}
	
	private String numberToString(int number){
		String ret = String.valueOf(number);
		while (ret.length() < 2){
			ret = "0" + ret;
		}
		return ret;
	}
	
	public void run(){
		while (true){
			try {
//				if (toPlay && !player.stoped){
//					currentDuration = player.getPosition() + plusDuration;
//					main.setTitle(playingSong + " [" + 0 + " | " + toDuaration(currentDuration) + " / " + toDuaration(duration) + "] [" + configure.title + "]");
//					main.startDuration.setText("  " + toDuaration(currentDuration));
//					if (!dragged) main.setValue(currentDuration);
//				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				main.setTitle(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	public void next() throws MalformedURLException, IOException, JavaLayerException, InterruptedException{
		if (configure.repeat.equals("Random")){
			index = random.nextInt(configure.songs.size());
			play(index);
			return;
		}
		if (index + 1 >= configure.songs.size()){
			if (configure.repeat.equals("All song")){
				play(index = 0);
				return;
			}else{
				toPlay = false;
				return;
			}
		}
		play(++index);
	}
	
	public void previous() throws MalformedURLException, IOException, JavaLayerException, InterruptedException{
		System.err.println(index);
		if (index <= 0) return;
		play(--index);
	}
	
	public int durationToSize(int duration){
		return (int) ((duration/1000.0 * 0) /(this.duration / 1000));
	}
	
	public void reset(){
		main.endDuration.setText("00:00");
		main.startDuration.setText("00:00");
		main.setValue(0);
		main.setRange(0);
	}

	public void seek(int value){
		//player.seek(value);
	}

	public void saveFile(String file) {
//		try{
//			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
//			//out.write(bytes, 0, length);
//			out.flush();
//			out.close();
//			main.setStatus("SAVE COMPLETED");
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
