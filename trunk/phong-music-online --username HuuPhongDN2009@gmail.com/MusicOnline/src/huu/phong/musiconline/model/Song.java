package huu.phong.musiconline.model;

import java.util.Map;

public abstract class Song extends Media implements ISong {

	private static final long serialVersionUID = 5628072748504915110L;

	@Override
	public Map<Format, String> getDirectLinks() {
		return null;
	}
}
