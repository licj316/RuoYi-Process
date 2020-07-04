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
public class FlowData extends BaseEntity {
	@Comment("id")
	public String id;
	@Comment("流程实例ID")
	public String procInsId;
	@Comment("流程任务ID")
	public String taskId;
	@Comment("流程变量键")
	public String name;
	@Comment("流程变量值")
	public String text;

}
