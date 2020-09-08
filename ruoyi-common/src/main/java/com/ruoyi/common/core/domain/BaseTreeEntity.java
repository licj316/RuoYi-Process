/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.common.core.domain;

import com.ruoyi.common.annotation.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

import java.io.Serializable;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseTreeEntity extends BaseEntity implements Serializable {

	@Comment("主键")
	protected Long id;

	@Comment("上级节点")
	protected Long pid;

	@Comment("上级节点名称")
	protected String pName;

	protected int shortNo;

	protected List children;

}
