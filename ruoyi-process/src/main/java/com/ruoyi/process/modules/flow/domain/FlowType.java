/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.domain;

import com.ruoyi.common.core.domain.BaseTreeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Comment;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/15
 * 流程分类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowType extends BaseTreeEntity {

    @Comment("类型名称")
    private String name;

    @Comment("虚拟节点不可选")
    private boolean virtualNode;
}
