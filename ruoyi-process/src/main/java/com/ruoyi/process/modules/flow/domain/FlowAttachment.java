package com.ruoyi.process.modules.flow.domain;

import com.ruoyi.common.annotation.Comment;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowAttachment extends BaseEntity {
	@Comment("id")
	public String id;
	@Comment("流程定义key")
	public String procDefKey;
	@Comment("流程实例ID")
	public String procInsId;
	@Comment("附件类型")
	public String attachType;

}
