package com.wqb.springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_param")
public class SystemParam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String paramKey;

    @Column(nullable = false, length = 500)
    private String paramValue;

    @Column(length = 100)
    private String description;

    @Column(name = "param_group", length = 32)
    private String groupName;

    public SystemParam() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getParamKey() { return paramKey; }
    public void setParamKey(String paramKey) { this.paramKey = paramKey; }
    public String getParamValue() { return paramValue; }
    public void setParamValue(String paramValue) { this.paramValue = paramValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
