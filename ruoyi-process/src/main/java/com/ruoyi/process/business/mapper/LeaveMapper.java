package com.ruoyi.process.business.mapper;

import com.ruoyi.process.business.domain.Leave;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveMapper {

	void save(Leave leave);

	Leave findById(String id);
}
