package com.ruoyi.process.core.plugin.flowable.service;


import com.alibaba.fastjson.JSONObject;
import org.flowable.engine.repository.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @Author yiyoung
 * @date 2020/4/21
 */
public interface ProcessDesignService {
    
    void createModel(String key,String name, String category, String descp) throws UnsupportedEncodingException;
    
    List<Map<String, Object>> listModelPage(Map<String, Object> params);
    
    void deleteModel(String modelId);
    
    String deployModel(String modelId) throws Exception;


    JSONObject getEditorXml(@PathVariable String modelId);

    void saveModelXml(@PathVariable String modelId,
                      @RequestBody Map<String, String> values);
}
