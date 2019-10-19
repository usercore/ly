package com.ly.task.impl.company;


import com.ly.task.impl.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.ly.task.ITaskService;
import com.ly.util.RedisUtil;

@Service("firstPageList")
public class FirstPageService implements ITaskService{

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
		pageUtil.parseSecPage(dealUrl,redisKey,"secPageList");
	}
	
	
	

}
