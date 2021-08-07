package com.fund.vo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class Company {

    private String code;

    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String companyName;
}
