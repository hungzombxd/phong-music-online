package huu.phong.musiconline.model;

import java.util.ArrayList;

public class History<T> extends ArrayList<T> {
	public int maxSize = 10;
	public int redo = 0;
	public int undo = 0;
	public int redoIndex = -1;
	public int undoIndex = -1;
	public T result = null;
	public boolean action = false;
	
	private static final long serialVersionUID = 6997084527209941815L;
	
	public History(){
		super(10);
	}
	
	public History(int number){
		super(number);
		this.maxSize = number;
	}
	
	public boolean add(T item){
		if (action) {
			action = false;
			return false;
		}
		if (redo > 0){
			for (int i = redoIndex; i >= 0; i--){
				remove(i);
			}
		}
		if (maxSize == size()){
			remove(maxSize - 1);
		}
		super.add(0, item);
		if (size() > 1){
			redo = 0;
			redoIndex = -1;
			undo = size() - 1;
			undoIndex = 1;
		}else{
			redo = 0;
			redoIndex = -1;
			undo = 0;
			undoIndex = -1;
		}
		return true;
	}

	public boolean redo(){
		if (redo <= 0) return false;
		redo--;
		undo++;
		undoIndex--;
		result = get(redoIndex--);
		action = true;
		return true;
	}
	
	public boolean undo(){
		if (undo <= 0) return false;
		undo--;
		redo++;
		redoIndex++;
		result = get(undoIndex++);
		action = true;
		return true;
	}
}
