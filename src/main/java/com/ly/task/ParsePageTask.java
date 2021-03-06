package com.ly.task;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ly.capture.IParsePageInfo;
import com.ly.util.RedisUtil;

@Service
public class ParsePageTask  {

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private RedisTemplate redisTemplate;
	
	private String[] tasks = {//"initTask","firstPageList","secPageList","thrPageList","initBrandTask","firstBrandPageList",
			"secBrandPageList"};
//	private String[] tasks = {"initTask"};



	@Autowired
	IParsePageInfo parseAmazonUkPage;
	
	public void run() throws Exception {
		
		for(int i=0;i<tasks.length;i++){
			TaskManager taskManager = new TaskManager(tasks[i]);
			Thread thread = new Thread(taskManager);
			thread.start();
		}
		
		/*// stringRedisTemplate的操作
		// String读写
		System.out.println("aaaaa");
		redisTemplate.delete("myStr");
		redisTemplate.opsForValue().set("myStr", "skyLine");
		System.out.println(redisTemplate.opsForValue().get("myStr"));
		System.out.println("---------------");

		// List读写
		redisTemplate.delete("myList");
		redisTemplate.opsForList().rightPush("myList", "T");
		redisTemplate.opsForList().rightPush("myList", "L");
		redisTemplate.opsForList().leftPush("myList", "A");
		List<String> listCache = redisTemplate.opsForList().range("myList", 0, -1);
		for (String s : listCache) {
			System.out.println(s);
		}
		System.out.println("---------------");

		// Set读写
		redisTemplate.delete("mySet");
		redisTemplate.opsForSet().add("mySet", "A");
		redisTemplate.opsForSet().add("mySet", "B");
		redisTemplate.opsForSet().add("mySet", "C");
		Set<String> setCache = redisTemplate.opsForSet().members("mySet");
		for (String s : setCache) {
			System.out.println(s);
		}
		System.out.println("---------------");

		// Hash读写
		redisTemplate.delete("myHash");
		redisTemplate.opsForHash().put("myHash", "BJ", "北京");
		redisTemplate.opsForHash().put("myHash", "SH", "上海");
		redisTemplate.opsForHash().put("myHash", "HN", "河南");
		Map<String, String> hashCache = redisTemplate.opsForHash().entries("myHash");
		for (Map.Entry entry : hashCache.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}
		System.out.println("---------------");*/

	}
}
