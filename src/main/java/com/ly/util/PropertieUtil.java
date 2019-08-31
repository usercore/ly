package com.ly.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertieUtil {

	
	private static Properties prop = new Properties();
	static {
		InputStream in = PropertieUtil.class.getResourceAsStream("/conf/config.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getValue(String key){
	    //获取key对应的value值
	   return prop.getProperty(key);
	}
	
}
