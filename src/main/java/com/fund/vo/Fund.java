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

    /**
     * 编码
     */
    private String code;

    /**
     * 类型
     */
    private String type;

    /**
     * 类型名称
     */
    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String typeName;

    /**
     * 规模
     */
    private String scale;

    /**
     * 基金经理
     */
    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private List<String> manager;

    /**
     * 管理人
     */
    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String managerCompany;

    /**
     * 	基金评级
     */
    private String  level;

    /**
     * 创建时间
     */
    private String establishedTime;

    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String fundName;

    /**
     * 公司名称
     */
    @Field(analyzer = "ik_smart", type = FieldType.Object)
    private List<Company> companyBeanList;
}