package com.ly.task;


import com.ly.util.SpringUtil;
import com.ly.util.SpringUtil2;

public class TaskManager implements  Runnable {
	
	String redisKey ;
	ITaskService taskService;
	
	SpringUtil springUtil;
	
	public TaskManager(String redisKey){
		this.redisKey = redisKey;
		taskService = (ITaskService)SpringUtil2.getBean(redisKey);
	}
	@Override
	public void run() {
		while(true){
			
			taskService.runTask(redisKey);
			
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
