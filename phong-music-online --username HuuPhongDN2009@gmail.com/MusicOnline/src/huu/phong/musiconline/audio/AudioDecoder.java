package huu.phong.musiconline.audio;

import huu.phong.musiconline.model.AudioInfo;

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
