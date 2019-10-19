package com.ly.task.impl.brand;
import com.alibaba.druid.util.StringUtils;
import com.ly.task.ITaskService;
import com.ly.task.impl.PageUtil;
import com.ly.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("initBrandTask")
public class InitBrandTaskService implements ITaskService {
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
        pageUtil.parseFirstPage(dealInfo,redisKey,"firstBrandPageList");
    }
}
