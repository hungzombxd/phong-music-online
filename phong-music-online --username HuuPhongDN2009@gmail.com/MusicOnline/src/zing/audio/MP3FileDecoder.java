package zing.audio;

import javax.sound.sampled.AudioFormat;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Equalizer;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class MP3FileDecoder implements AudioDecoder{
	private Bitstream bitstream;
	private Header header;
	private Decoder decoder;
	private AudioFormat fmt;
	private SampleBuffer output;
	private Equalizer equalizer;
	private AudioStream in;
	private AudioInfo audioInfo;
	private int duration;
	private int metaDataLength;
	private Object locked = new Object();
	private boolean seeking = false;
	
	public MP3FileDecoder(AudioStream in) {
		this.in = in;
		bitstream = new Bitstream(in);
		equalizer = new Equalizer();
		decoder = new Decoder();
		decoder.setEqualizer(equalizer);
		try {
			header = bitstream.readFrame();
			metaDataLength = bitstream.header_pos();
		} catch (BitstreamException e) {
			e.printStackTrace();
		}
		if (header == null) throw new RuntimeException("Can not get header");
		fmt = new AudioFormat(header.frequency(), 16, header.mode() == Header.SINGLE_CHANNEL ? 1 : 2, true, false);
		duration = (int) header.total_ms(in.getLength() - metaDataLength);
		audioInfo = new AudioInfo("MP3", header.vbr(), header.bitrate() / 1000, header.frequency(), header.mode() == Header.SINGLE_CHANNEL ? 1 : 2, in.getLength(), duration);
	}

	public AudioFormat getAudioFormat() {
		return fmt;
	}

	public synchronized int getPCMData(byte[] buffer) {
		if (seeking){
			synchronized (locked) {
				try {
					locked.wait();
					bitstream.closeFrame();
					try {
						header = bitstream.readFrame();
					} catch (BitstreamException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (header == null) return -1;
		try {
			output = (SampleBuffer)decoder.decodeFrame(header, bitstream);
		} catch (DecoderException e) {
			e.printStackTrace();
		} finally {
			bitstream.closeFrame();
			try {
				header = bitstream.readFrame();
			} catch (BitstreamException e) {
				e.printStackTrace();
			}
		}
		toPCMByte(buffer, output.getBuffer(), 0, output.getBufferLength());
		return output.getBufferLength() * 2;
	}

	private void toPCMByte(byte[] buffer, short[] samples, int offs, int len) {
		int idx = 0;
		short s;
		while (len-- > 0) {
			s = samples[offs++];
			buffer[idx++] = (byte) s;
			buffer[idx++] = (byte) (s >>> 8);
		}
	}

	public void seek(int size) {
		seeking = false;
		in.seek(size);
		seeking = false;
		synchronized (locked) {
			locked.notifyAll();
		}
	}

	public AudioInfo getAudioInfo() {
		return audioInfo;
	}

	public int getDuration() {
		return duration;
	}
	
	public boolean seekable() {
		return true;
	}

	public int durationToSize(int duration) {
		if (audioInfo == null) return 0;
		return (int) (audioInfo.getBitrate() / 8.0 * duration);
	}

	public int sizeToDuration(int size) {
		if (header == null) return 0;
		return (int) header.total_ms(size);
	}
}
