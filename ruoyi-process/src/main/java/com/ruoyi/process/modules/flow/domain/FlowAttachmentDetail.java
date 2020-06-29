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
public class FlowAttachmentDetail extends BaseEntity {
	@Comment("id")
	public String id;
	@Comment("流程附件表ID")
	public String flowAttachmentId;
	@Comment("文件原始")
	public String fileName;
	@Comment("保存文件实际名称")
	public String saveFileName;
	@Comment("文件类型")
	public String fileType;
	@Comment("文件大小（byte）")
	public Long fileSize;
	@Comment("文件存储路径")
	public String filePath;
	@Comment("删除标志（0代表存在 2代表删除）")
	public String delFlag;
	@Comment("备注")
	public String remark;
}
