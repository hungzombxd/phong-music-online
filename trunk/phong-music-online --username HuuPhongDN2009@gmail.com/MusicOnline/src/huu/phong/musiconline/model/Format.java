package huu.phong.musiconline.model;

import javax.swing.ImageIcon;

public enum Format {
	MP3_128_KBPS("128kbps.png", "128"), MP3_320_KBPS("320kbps.png", "320"), LOSSLESS("lossless.png", "lossless");
	
	private final ImageIcon image;
	private final String quality;
	
	private Format(String image, String quality){
		this.image = new ImageIcon(Format.class.getResource("/images/" + image));
		this.quality = quality;
	}
	
	public ImageIcon getImage(){
		return image;
	}
	
	public static Format getFormat(String quality){
		for (Format format : values()){
			if(format.quality.equals(quality)){
				return format;
			}
		}
		return null;
	}
}
