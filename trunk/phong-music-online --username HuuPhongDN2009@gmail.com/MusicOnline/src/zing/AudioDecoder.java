package zing;

import javax.sound.sampled.AudioFormat;

public interface AudioDecoder {

	AudioFormat getAudioFormat();
	
	int getPCMData(byte[] buffer);
	
	boolean seekable();
	
	int seek(int time);
	
	AudioInfo getAudioInfo();

	int getDuration();

	int durationToSize(int duration);
	
	int sizeToDuration(int size);
}
