package com.fund.dao;

import com.fund.vo.Fund;
import org.springframework.data.repository.CrudRepository;

public interface FundDao extends CrudRepository<Fund, Long> {
}