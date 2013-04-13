package zing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class SongRenderer implements ListCellRenderer{
	ImageIcon first = new ImageIcon(SongRenderer.class.getResource("/images/song.png"));
	Color odd = new Color(238, 238, 238);
	Color even = Color.WHITE;
	Color selected = new Color(51, 153, 255);
	Dimension dimension;
	public static final int VIEW_MODE_CLASSIC = 1;
	public static final int VIEW_MODE_DETAIL = 0;
	public static final ImageIcon MP3_320_KBPS = new ImageIcon(Album.class.getResource("/images/mp3.png"));
	public static final ImageIcon LOSSLESS = new ImageIcon(Album.class.getResource("/images/lossless.png"));
	
	public SongRenderer(){
		
	}
	
	private String numberToString(int number){
		String ret = String.valueOf(number);
		while (ret.length() < 2){
			ret = "0" + ret;
		}
		return ret;
	}
	
	private Component viewClassic(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
		Song song = (Song) value;
		JLabel both = new JLabel();
		both.setOpaque(true);
		both.setLayout(new BoxLayout(both, BoxLayout.X_AXIS));
		JLabel label = new JLabel(song.title);
		JLabel hq = null;
		JLabel number = new JLabel(" " + numberToString(index + 1) + ".");
		number.setForeground(Color.BLUE);
		switch (song.getQuality()) {
		case Song.LOSSLESS:
			hq = new JLabel(LOSSLESS);
			hq.setText(" ");
			break;

		case Song.MP3_320_KBPS:
			hq = new JLabel(MP3_320_KBPS);
			hq.setText(" ");
			break;
			
		default:
			break;
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
		label.setIcon(first);
		both.add(number, BorderLayout.WEST);
		both.add(label, BorderLayout.CENTER);
		if (hq != null) both.add(hq, BorderLayout.EAST);
		dimension = list.getVisibleRect().getSize();
		dimension.height = 25;
		both.setPreferredSize(dimension);
		dimension.width = dimension.width - MP3_320_KBPS.getIconWidth() - number.getPreferredSize().width - 5;
		label.setPreferredSize(dimension);
		label.setMaximumSize(dimension);
		label.setMinimumSize(dimension);
		return both;
	}
	
	private Component viewDetail(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
		Song song = (Song) value;
		JLabel both = new JLabel();
		both.setOpaque(true);
		both.setLayout(new BoxLayout(both, BoxLayout.X_AXIS));
		JLabel label = new JLabel(value.toString());
		JLabel hq = null;
		JLabel number = new JLabel(numberToString(index + 1) + ".");
		number.setForeground(Color.BLUE);
		number.setIcon(first);
		number.setVerticalTextPosition(JLabel.CENTER);
		number.setHorizontalTextPosition(JLabel.LEFT);
		number.setIconTextGap(1);
		number.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		switch (song.getQuality()) {
		case Song.LOSSLESS:
			hq = new JLabel(LOSSLESS);
			hq.setText(" ");
			break;

		case Song.MP3_320_KBPS:
			hq = new JLabel(MP3_320_KBPS);
			hq.setText(" ");
			break;
			
		default:
			break;
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
		both.add(number, BorderLayout.WEST);
		both.add(label, BorderLayout.CENTER);
		if (hq != null) both.add(hq, BorderLayout.EAST);
		dimension = both.getPreferredSize();
		dimension.height = 55;
		both.setPreferredSize(dimension);
		return both;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		switch (Configure.getInstance().viewModeSong) {
		case VIEW_MODE_CLASSIC:
			return viewClassic(list, value, index, isSelected, cellHasFocus);
		case VIEW_MODE_DETAIL:
			return viewDetail(list, value, index, isSelected, cellHasFocus);
		default:
			return viewDetail(list, value, index, isSelected, cellHasFocus);
		}
	}
}
