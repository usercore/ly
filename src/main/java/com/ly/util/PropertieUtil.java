package com.ly.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertieUtil {

	public static String getValue(String key){
		Properties properties = new Properties();
		String filePath = System.getProperty("user.dir") + "/conf/config.properties";
		// 使用ClassLoader加载properties配置文件生成对应的输入流
		InputStream in;
		  // 使用properties对象加载输入流
		try {
			in = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(in);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    //获取key对应的value值
	   return properties.getProperty(key);
	}
	
}
