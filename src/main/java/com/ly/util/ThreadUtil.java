package com.ly.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThreadUtil {
	
	public static void sleepTime(Long time){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss E");
		LocalDateTime ldt = LocalDateTime.now();
		String date = ldt.format(dtf);
		System.out.println(date + "发生异常，休息5分钟");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
