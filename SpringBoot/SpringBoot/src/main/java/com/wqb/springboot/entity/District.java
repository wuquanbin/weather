package com.wqb.springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "district")
public class District extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false, length = 128)
    private String serviceArea;

    @Column(length = 255)
    private String highlights;

    @Column(length = 64)
    private String transportFocus;

    @Column()
    private Double latitude;

    @Column()
    private Double longitude;

    @Column(length = 20)
    private String adminCode;

    @Column(length = 50)
    private String parentRegion;

    public District() {
    }

    public District(String code, String name, String serviceArea, String highlights, String transportFocus) {
        this.code = code;
        this.name = name;
        this.serviceArea = serviceArea;
        this.highlights = highlights;
        this.transportFocus = transportFocus;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
    public String getHighlights() { return highlights; }
    public void setHighlights(String highlights) { this.highlights = highlights; }
    public String getTransportFocus() { return transportFocus; }
    public void setTransportFocus(String transportFocus) { this.transportFocus = transportFocus; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }
    public String getParentRegion() { return parentRegion; }
    public void setParentRegion(String parentRegion) { this.parentRegion = parentRegion; }
}
