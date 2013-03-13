package zing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

public class ColorSlider extends JComponent{
	private static final long serialVersionUID = 1495787053831058306L;
	Color rangeColor = new Color(230, 230, 230);
	Color normalColor = Color.WHITE;//new Color(240, 240, 240);
	Color valueColor = new Color(66, 174, 227);
	Color borderColor = new Color(204, 204, 204);
	Color rangeTextColor = new Color(163, 73, 164);
	private int range = 0;
	int rangeWidth = 0;
	int valueWidth = 0;
	int x = 1;
	int y = 0;
	int value = 0;
	int min = 0;
	int max = 100;
	int width = 220;
	int height = 25;
	boolean dragging = false;
	
	public ColorSlider(){
		super();
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setSize(width, height);
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setInitialDelay(0);
        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
            	if (!SwingUtilities.isLeftMouseButton(e));
            	setValue(valueAtPoint(e));
            	dragging = true;
            }
		});
        addMouseMotionListener(new MouseAdapter() {
        	public void mouseMoved(MouseEvent e){
				setToolTipText(AudioPlayer.toDuaration(valueAtPoint(e)));
			}
			public void mouseDragged(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) return;
		        setValue(valueAtPoint(e));
		        setToolTipText(AudioPlayer.toDuaration(getValue()));
		        MouseEvent event = new MouseEvent(
                        ColorSlider.this,
                        MouseEvent.MOUSE_MOVED,
                        System.currentTimeMillis(),
                        0,
                        e.getX(),
                        e.getY(),
                        0,
                        false);
                ToolTipManager.sharedInstance().mouseMoved(event);
			}
		});
	}
	
	public void setRange(int range){
		this.range = range;
		this.repaint();
	}
	
	public int getRange(){
		return range;
	}
	
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		Color oldColor = g.getColor();
		y = height / 2 - 3;
		g.setColor(borderColor);
		g.drawRect(0, y - 1, width - 1, 5);
		g.setColor(normalColor);
		g.fillRect(x, y, width - 2, 4);
		rangeWidth = (this.range * (width - 2)) / max;
		g.setColor(rangeColor);
		if (rangeWidth > x) g.fillRect(x, y, rangeWidth, 4);
		if(range < max && rangeWidth > 0){
			g.setColor(rangeTextColor);
			if (rangeWidth + 27 > width){
				g.drawString(AudioPlayer.toDuaration(this.range), width - 27, y - 1);
			}else{
				g.drawString(AudioPlayer.toDuaration(this.range), rangeWidth, y - 1);
			}
		}
		valueWidth = (getValue() * (width - 2)) / max;
		g.setColor(valueColor);
		if (valueWidth > x) g.fillRect(x, y, valueWidth, 4);
		g.setColor(oldColor);
	}
	
	public String durationAtMouse(MouseEvent e){
		return AudioPlayer.toDuaration(valueAtPoint(e));
	}
	
	public int valueAtPoint(MouseEvent e){
		int value = (e.getPoint().x - 1) * max / (width - 2);
		if (value < 0) {
			return 0;
		}else if(value > max){
			return max;
		}
		return value;
	}
	
	public void setValue(int value){
		this.value = value;
		repaint();
	}
	
	public int getValue(){
		return this.value;
	}
}
