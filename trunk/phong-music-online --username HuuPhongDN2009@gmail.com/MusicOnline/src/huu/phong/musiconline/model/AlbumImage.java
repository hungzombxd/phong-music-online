package huu.phong.musiconline.model;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.URL;

import javax.swing.ImageIcon;

public class AlbumImage extends ImageIcon {

	private static final long serialVersionUID = 2122710715121355814L;
	private static final int DEFAULT_WIDTH = 94;
	private static final int DEFAULT_HEIGHT = 94;
	
	public AlbumImage(URL url){
		super(url);
	}
	
	public synchronized void paintIcon(Component c, Graphics g, int x, int y){
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.drawImage(getImage(), 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, getImageObserver());
	}
	
	public int getIconWidth(){
		return DEFAULT_WIDTH;
	}
	
	public int getIconHeight(){
		return DEFAULT_HEIGHT;
	}
}
