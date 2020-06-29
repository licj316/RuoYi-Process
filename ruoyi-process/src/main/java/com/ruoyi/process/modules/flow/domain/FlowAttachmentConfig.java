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
public class FlowAttachmentConfig extends BaseEntity {
	@Comment("id")
	public String id;
	@Comment("流程定义key")
	public String procDefKey;
	@Comment("附件类型")
	public String attachType;
	@Comment("删除标志（0代表存在 2代表删除）")
	public String delFlag;
}
