package zing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable{
	private static final long serialVersionUID = -8700562936652043506L;
	public List<Song> lists;
	public String title;
	
	public Playlist(String title){
		this.title = title;
		lists = new ArrayList<Song>();
	}
	
	public Playlist(){
		lists = new ArrayList<Song>();
	}
	
	public void add(Song song){
		lists.add(song);
	}
	
	public boolean contain(Song song){
		return lists.contains(song);
	}
	
	public void remove(Song song){
		lists.remove(song);
	}
	
	public void remove(int index){
		lists.remove(index);
	}
	
	public boolean equals(Object object){
		boolean ret = false;
		if (object instanceof Playlist){
			Playlist playlist = (Playlist) object;
			if (playlist.title.equals(title)){
				ret = true;
			}
		}
		return ret;
	}
	
	public String toString(){
		return title;
	}
}
