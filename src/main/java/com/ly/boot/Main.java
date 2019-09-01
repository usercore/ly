package com.ly.boot;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	public static void main(String[] args) throws IOException {

		ClassPathXmlApplicationContext context;
			
		context = new ClassPathXmlApplicationContext(new String[] { "applicationContext-redis.xml" });
		context.start();
		synchronized (Main.class) {
			while (true) {
				try {
					Main.class.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
