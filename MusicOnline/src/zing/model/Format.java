package zing.model;

import javax.swing.ImageIcon;

public enum Format {
	MP3_128_KBPS("128kbps.png"), MP3_320_KBPS("320kbps.png"), LOSSLESS("lossless.png");
	
	private final ImageIcon image;
	
	private Format(String image){
		this.image = new ImageIcon(Format.class.getResource("/images/" + image));
	}
	
	public ImageIcon getImage(){
		return image;
	}
}
