package com.wqb.springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "operation_log")
public class OperationLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String operatorName;

    @Column(nullable = false, length = 32)
    private String operationType;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(length = 255)
    private String description;

    @Column(length = 500)
    private String requestParams;

    @Column(length = 64)
    private String ip;

    @Column(nullable = false)
    private Integer status = 1;

    public OperationLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
