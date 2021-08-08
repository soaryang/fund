package com.fund.vo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
public class Company implements Serializable {

    private String code;

    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String companyName;
}
