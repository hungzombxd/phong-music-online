package zing;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Utils extends Thread{
	
	private Robot robot;
	private Clipboard clipboard;
	private boolean hasYahooMessenger = false;
	private String[] ansi = new String[]{"a", "a", "a", "a", "a", "a", "a",
			"a", "a", "a", "a", "a", "a", "a", "a", "o", "o", "o", "o", "o",
			"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "u", "u", "u",
			"u", "u", "d", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e",
			"i", "i", "i", "i", "i", "u", "u", "u", "u", "u", "y", "y", "y",
			"y", "y", "o", "e", "o", "u", "a", "a"};
	private String[] utf8  = new String[] { "á", "à", "ả", "ạ", "ã", "ắ", "ằ",
					"ặ", "ẳ", "ẵ", "ấ", "ầ", "ẩ", "ẫ", "ậ", "ớ", "ờ", "ợ", "ở", "ỡ",
					"ó", "ò", "ọ", "ỏ", "õ", "ố", "ồ", "ộ", "ổ", "ỗ", "ứ", "ừ", "ự",
					"ử", "ữ", "đ", "é", "è", "ẹ", "ẻ", "ẽ", "ế", "ề", "ệ", "ể", "ễ",
					"í", "ì", "ị", "ỉ", "ĩ", "ú", "ù", "ụ", "ủ", "ũ", "ý", "ỳ", "ỵ",
					"ỷ", "ỹ", "ô", "ê", "ơ", "ư", "ă", "â" };
	public Utils() throws AWTException{
		robot = new Robot();
		this.start();
	}
	
	public String toANSI(String str){
		for (int i = 0; i < utf8.length; i++) {
			str = str.replaceAll(utf8[i], ansi[i]);
			str = str.replaceAll(utf8[i].toUpperCase(), ansi[i].toUpperCase());
		}
		return str;
	}
	
	public void run(){
		hasYahooMessenger = hasYahooMessenger();
		while(true){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			hasYahooMessenger = hasYahooMessenger();
		}
	}
	
	public void sendKeys(int...keys) throws AWTException{
		for (int key:keys){
			robot.keyPress(key);
		}
		for (int key:keys){
			robot.keyRelease(key);
		}
	}
	
	public void sendStatus(String content, String link) throws AWTException{
		if (!hasYahooMessenger) return;
		sendKeys(KeyEvent.VK_WINDOWS, KeyEvent.VK_SHIFT, KeyEvent.VK_Y);
		robot.delay(200);
		clipboard.setContents(new StringSelection(content), null);
		sendKeys(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
		sendKeys(KeyEvent.VK_TAB);
		robot.delay(200);
		try {
			clipboard.setContents(new StringSelection(link), null);
		}catch (Exception e) {
			clipboard.setContents(new StringSelection(link), null);
			e.printStackTrace();
		}
		sendKeys(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
		sendKeys(KeyEvent.VK_ENTER);
	}
	
	public void setClipboard(Clipboard clipboard){
		this.clipboard = clipboard;
	}
	
	private boolean hasYahooMessenger(){
		boolean ret = false;
		try {
	        String line;
	        Process p = Runtime.getRuntime().exec("tasklist.exe");
	        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	            if (line.contains("YahooMessenger.exe")){
	            	ret = true;
	            	break;
	            }
	        }
	        input.close();
	    } catch (Exception err) {
	        err.printStackTrace();
	    }
		return ret;
	}
}
