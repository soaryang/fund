package com.fund.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

@Data
@Document(indexName = "fund", type = "fund")
public class Fund implements Serializable {

    @Id
    private long id;

    private String code;

    private String type;

    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String fundName;

    @Field(analyzer = "ik_smart", type = FieldType.Object)
    private List<Company> companyBeanList;
}