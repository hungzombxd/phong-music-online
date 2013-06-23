package zing.model;

import java.io.Serializable;

public class ItemCombo implements Serializable {
	private static final long serialVersionUID = -8299958599947591679L;
	public String text;
	public String value;
	
	public ItemCombo(){
		
	}
	
	public ItemCombo(String text){
		this.text = text;
	}
	
	public ItemCombo(String text, String value){
		this.text = text;
		this.value = value;
	}
	
	public String toString(){
		return text;
	}
	
	public boolean equals(Object obj){
		if (!(obj instanceof ItemCombo)) return false;
		return text.equals(((ItemCombo)obj).text);
	}
}
