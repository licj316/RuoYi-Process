/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.utils;

import com.ruoyi.process.core.plugin.flowable.util.FlowDiagramUtils;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author : 黄川
 */
public class JsContex {

    private static final Logger log = LogManager.getLogger(FlowDiagramUtils.class);

    private static ClassLoader                ccl     = Thread.currentThread().getContextClassLoader();
    private static NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    private static transient ThreadLocal<NashornScriptEngine> jsEngineThreadLocal = new ThreadLocal<>();

    /**
     * 创建引擎
     *
     * @param jsEngine
     */
    public static synchronized void set(NashornScriptEngine jsEngine) {
        jsEngineThreadLocal.set(jsEngine);
    }

    /**
     * 取得引擎
     *
     * @return
     */
    public static synchronized NashornScriptEngine get() {
        NashornScriptEngine jsEngine = jsEngineThreadLocal.get();
        //双重检查
        if (jsEngine == null) {
            synchronized (JsContex.class) {
                if (jsEngine == null) {
                    NashornScriptEngine engine = (NashornScriptEngine) factory.getScriptEngine(new String[]{"-scripting",
                            "-nse",
                    }, ccl, (s) -> false);
                    jsEngine = engine;
                    set(engine);
                }
            }
        }
        return jsEngine;
    }

    /**
     * 释放
     */
    public static void destory() {
        jsEngineThreadLocal.remove();
    }


    /**
     * @param javaScript 例子 value = param.name +parm.num ;
     * @param context
     * @return
     */
    public static String evalJavaScript(String javaScript, Map<String,Object> context) {
        StringBuffer jsCode = new StringBuffer();
        jsCode.append("function getText(ctx){ var value=''; ");
        jsCode.append(javaScript);
        jsCode.append(" return value; }");
        String value = "";
        try {
            JsContex.get().compile(jsCode.toString());
            JsContex.get().eval(jsCode.toString());
            Object result = JsContex.get().invokeFunction("getText", context);
            value = String.valueOf(result);
        } catch (Exception e) {
            log.error("解析动态JS错误", e);
        }
        return value;
    }


}