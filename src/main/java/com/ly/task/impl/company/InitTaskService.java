package com.ly.task.impl.company;

import java.util.HashMap;
import java.util.Map;

import com.ly.task.impl.PageUtil;
import com.ly.util.UrlUtil;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.druid.util.StringUtils;
import com.ly.capture.IParsePageInfo;
import com.ly.task.ITaskService;
import com.ly.util.GloableConstant;
import com.ly.util.HttpUtil;
import com.ly.util.RedisUtil;

@Service("initTask")
public class InitTaskService implements ITaskService {

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	PageUtil pageUtil;

	@Override
	public void runTask(String redisKey) {
		String dealInfo = redisUtil.lPop(redisKey);
		if (StringUtils.isEmpty(dealInfo)) {
			return;
		}
		pageUtil.parseFirstPage(dealInfo,redisKey,"firstPageList");
	}
}
