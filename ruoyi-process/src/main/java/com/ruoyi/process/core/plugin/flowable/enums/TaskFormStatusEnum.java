/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.core.plugin.flowable.enums;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/26
 */
public enum TaskFormStatusEnum {

    EDIT("编辑"),

    VIEW("查看"),

    AUDIT("审核");

    static HashMap<String, TaskFormStatusEnum> lookup = new HashMap<>();

    static {
        for (TaskFormStatusEnum typeEnum : EnumSet.allOf(TaskFormStatusEnum.class)) {
            lookup.put(typeEnum.toString(), typeEnum);
        }
    }

    String value;

    public static TaskFormStatusEnum get(String value) {
        return lookup.get(value);
    }

    TaskFormStatusEnum(String value) {
        this.value = value;
    }

    public static HashMap<String, TaskFormStatusEnum> getLookup() {
        return lookup;
    }

    public String getValue() {
        return value;
    }
}
