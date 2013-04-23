package zing.model;

public class AudioInfo {
	private String type;
	private int bitrate;
	private int frequency;
	private int channel;
	private int length;
	private int duration;
	private boolean vbr;
	
	public AudioInfo(){
		
	}
	
	public AudioInfo(String type, boolean vbr, int bitrate, int frequency, int channel,
			int length, int duration) {
		super();
		this.type = type;
		this.vbr = vbr;
		this.bitrate = bitrate;
		this.frequency = frequency;
		this.channel = channel;
		this.length = length;
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public boolean isVbr() {
		return vbr;
	}

	public void setVbr(boolean vbr) {
		this.vbr = vbr;
	}

	public String toString(){
		return type + " | " + (vbr ? "VBR" : "CBR") + " | " + bitrate + " Kbps" + " | " + frequency + " Hz" + " | " + channel + " Channel";
	}
}
