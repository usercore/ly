package com.ly.task.impl.brand;

import com.ly.capture.IParsePageInfo;
import com.ly.task.ITaskService;
import com.ly.task.impl.PageUtil;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.RedisUtil;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service("firstBrandPageList")
public class FirstBrandPageService implements ITaskService{

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	PageUtil pageUtil;

	@Override
	public void runTask(String redisKey) {
		
		System.out.println(redisKey);
		
		String dealUrl = redisUtil.lPop(redisKey);
		
		if(StringUtils.isEmpty(dealUrl)){
			return ;
		}
		pageUtil.parseSecPage(dealUrl,redisKey,"secBrandPageList");

	}
	
	
	

}
