package zing;

import javax.sound.sampled.AudioFormat;

public interface AudioDecoder {

	AudioFormat getAudioFormat();
	
	int getPCMData(byte[] buffer);
	
	boolean seekable();
	
	void seek(int size);
	
	AudioInfo getAudioInfo();

	int getDuration();

	int durationToSize(int duration);
	
	int sizeToDuration(int size);
}
