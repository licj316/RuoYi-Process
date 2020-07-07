package com.ruoyi.process.modules.flow.mapper;

import com.ruoyi.process.modules.flow.domain.FlowInstExtend;
import org.apache.fop.fo.pagination.Flow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FlowInstExtendMapper {

	void save(FlowInstExtend flowInstExtend);

	void update(FlowInstExtend flowInstExtend);

	void updateCurrTaskInfo(FlowInstExtend flowInstExtend);

	List<FlowInstExtend> findByParams(Map<String, Object> params);
}
