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
public class FlowInstExtend extends BaseEntity {

	@Comment("id")
	public String id;
	@Comment("流程定义ID")
	public String procDefId;
	@Comment("流程定义KEY")
	public String procDefKey;
	@Comment("流程实例ID")
	public String procInsId;
	@Comment("当前任务key")
	public String currTaskDefKey;
	@Comment("当前任务名称")
	public String currTaskName;
	@Comment("当前任务类型")
	public String currTaskType;
	@Comment("关键字一")
	public String keyOne;
	@Comment("关键字二")
	public String keyTwo;
	@Comment("关键字三")
	public String keyThree;
	@Comment("关键字四")
	public String keyFour;
	@Comment("关键字五")
	public String keyFive;
	@Comment("关键字六")
	public String keySix;
	@Comment("关键字七")
	public String keySeven;
}
