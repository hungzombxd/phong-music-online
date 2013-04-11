package zing;

import java.util.Stack;

public class Jobs extends Thread{
	
	private Stack<Runnable> jobs = new Stack<Runnable>();
	
	public Jobs(){
		
	}
	
	public void addJob(Runnable job){
		jobs.add(job);
	}
}
