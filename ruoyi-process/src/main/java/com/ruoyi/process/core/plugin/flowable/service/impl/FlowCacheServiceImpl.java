/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.service.impl;

import com.ruoyi.framework.helper.RedisHelper;
import com.ruoyi.process.core.plugin.flowable.config.NutzFwProcessEngineConfiguration;
import com.ruoyi.process.core.plugin.flowable.service.FlowCacheService;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/29
 */
@Service
public class FlowCacheServiceImpl implements FlowCacheService {

    static final String FLOW_ABLE_CACHE = "FLOW_ABLE_CACHE:";
    @Autowired
    RedisHelper redisHelper;
    @Autowired
	RepositoryService repositoryService;

    @Autowired
    NutzFwProcessEngineConfiguration processEngineConfiguration;

    @Override
    public ProcessDefinition getProcessDefinitionCache(String processDefinitionId) {
        DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
        DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = processDefinitionCache.get(processDefinitionId);
        if (processDefinitionCacheEntry == null) {
            return this.getProcessDefinitionRenewCache(processDefinitionId);
        }
        return processDefinitionCacheEntry.getProcessDefinition();
    }

    public ProcessDefinition getProcessDefinitionRenewCache(String processDefinitionId) {
        //服务关闭后缓存中的实例就被销毁了，需要更新下缓存
        repositoryService.getBpmnModel(processDefinitionId);
        DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
        DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = processDefinitionCache.get(processDefinitionId);
        return processDefinitionCacheEntry.getProcessDefinition();
    }

    @Override
    public Deployment getDeploymentCache(String deploymentId) {
        String key = RedisHelper.buildRediskey(FLOW_ABLE_CACHE, deploymentId);
        if (redisHelper.exists(key)) {
            return redisHelper.getBySerializable(key);
        } else {
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            redisHelper.setNXSerializable(key, deployment, RedisHelper.DEFAULT_SECOND);
            return deployment;
        }
    }

    @Override
    public void delCache() {
        String key = RedisHelper.buildRediskey(FLOW_ABLE_CACHE, "*");
        Set<String> lists = redisHelper.keys(key);
        if (lists.size() > 0) {
            redisHelper.del(lists.toArray(new String[]{}));
        }
    }
}
