package huu.phong.musiconline.model;

import huu.phong.musiconline.Configure;
import huu.phong.musiconline.model.zing.ZingAlbum;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class AlbumRenderer implements ListCellRenderer{
	public static final ImageIcon DEFAULT_DETAIL_ALBUM_ART = new ImageIcon(ZingAlbum.class.getResource("/images/default_albumart.jpg"));
	public static final ImageIcon DEFAULT_CLASSIC_ALBUM_ART = new ImageIcon(ZingAlbum.class.getResource("/images/album.png"));
	Color odd = new Color(238, 238, 238);
	Color even = Color.WHITE;
	Color selected = new Color(51, 153, 255);
	Color line = new Color(238, 238, 238);
	public static final int VIEW_MODE_CLASSIC = 1;
	public static final int VIEW_MODE_DETAIL = 0;
	Dimension dimension;
	Dimension imageSize = new Dimension(94, 94);
	Thread loadImages;
	Queue<Thread> process = new ArrayDeque<Thread>();
	
	public AlbumRenderer(){
		loadImages = new Thread(){
			public void run(){
				while (true){
					while (!process.isEmpty()){
						Thread thread = process.poll();
						thread.start();
						try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					synchronized (loadImages) {
						try {
							loadImages.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		loadImages.start();
	}
	
	private String numberToString(int number){
		String ret = String.valueOf(number);
		while (ret.length() < 2){
			ret = "0" + ret;
		}
		return ret;
	}
	
	private Component viewDetail(final JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
		final IAlbum album = (IAlbum) value;
		JLabel both = new JLabel();
		both.setLayout(new BoxLayout(both, BoxLayout.X_AXIS));
		both.setOpaque(true);
		both.setBackground(Color.WHITE);
		both.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, line));
		JLabel label = new JLabel(album.toString());
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		final JLabel icon = new JLabel();
		icon.setPreferredSize(imageSize);
		icon.setMinimumSize(imageSize);
		icon.setMaximumSize(imageSize);
		icon.setHorizontalAlignment(JLabel.CENTER);
//		icon.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
//		icon.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, line));
		if (album.hasThumbnail()){
			if (album.isThumbnailLoaded()){
				icon.setIcon(album.getThumbnail());
			}else{
				icon.setIcon(DEFAULT_DETAIL_ALBUM_ART);
				process.add(new Thread(){
					public void run(){
						icon.setIcon(album.getThumbnail());
						list.repaint();
					}
				});
				synchronized (loadImages) {
					loadImages.notifyAll();
				}
			}
		}else{
			icon.setIcon(DEFAULT_DETAIL_ALBUM_ART);
		}
		label.setVerticalTextPosition(JLabel.CENTER);
		both.add(icon, BorderLayout.WEST);
		both.add(label, BorderLayout.CENTER);
		if (Format.MP3_320_KBPS.compareTo(album.getQuality()) <= 0){
			both.add(new JLabel(" ", SongRenderer.MP3_320_KBPS, JLabel.LEADING), BorderLayout.EAST);
		}
		if (isSelected) {
			both.setBackground(selected);
			label.setForeground(Color.WHITE);
		}
		dimension = both.getPreferredSize();
		dimension.height = imageSize.height;
		both.setPreferredSize(dimension);
		return both;
	}
	
	private Component viewClassic(final JList list, Object value,int index, boolean isSelected, boolean cellHasFocus){
		final IAlbum album = (IAlbum) value;
		JLabel both = new JLabel();
		both.setOpaque(true);
		both.setLayout(new BoxLayout(both, BoxLayout.X_AXIS));
		JLabel label = new JLabel(album.getFullTitle());
		JLabel hq = null;
		JLabel number = new JLabel(" " + numberToString(index + 1) + ".");
		number.setForeground(Color.BLUE);
		if (Format.MP3_320_KBPS.compareTo(album.getQuality()) <= 0){
			hq = new JLabel(SongRenderer.MP3_320_KBPS);
			hq.setText(" ");
		}
		if (index % 2 == 1){
			both.setBackground(odd);
			if(hq != null) hq.setBackground(odd);
		}else{
			both.setBackground(even);
			if(hq != null) hq.setBackground(even);
		}
		if (isSelected) {
			both.setBackground(selected);
			label.setForeground(Color.WHITE);
			number.setForeground(Color.WHITE);
			if(hq != null) hq.setBackground(selected);
		}
		label.setIcon(DEFAULT_CLASSIC_ALBUM_ART);
		both.add(number, BorderLayout.WEST);
		both.add(label, BorderLayout.CENTER);
		if (hq != null) both.add(hq, BorderLayout.EAST);
		dimension = list.getVisibleRect().getSize();
		dimension.height = 25;
		both.setPreferredSize(dimension);
		dimension.width = dimension.width - SongRenderer.MP3_320_KBPS.getIconWidth() - number.getPreferredSize().width - 5;
		label.setPreferredSize(dimension);
		label.setMaximumSize(dimension);
		label.setMinimumSize(dimension);
		return both;
	}
	
	public Component getListCellRendererComponent(final JList list, Object value,int index, boolean isSelected, boolean cellHasFocus) {
		switch (Configure.getInstance().viewModeAlbum) {
		case VIEW_MODE_DETAIL:
			return viewDetail(list, value, index, isSelected, cellHasFocus);
		case VIEW_MODE_CLASSIC:
			return viewClassic(list, value, index, isSelected, cellHasFocus);
		default:
			return viewDetail(list, value, index, isSelected, cellHasFocus);
		}
	}
}
