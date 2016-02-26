package com.baldy.marklogic.test.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.style.ToStringCreator;

import com.baldy.marklogic.MarkLogicData;
import com.google.common.collect.Lists;

@XmlRootElement
@Entity(name = "taxi")
public class Taxi implements MarkLogicData {

    private static final List<String> COLLECTIONS = Lists.newArrayList("taxis");

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "plate_no")
    private String plateNo;

    @Column(name = "operator")
    private String operator;

    @Transient
    private Map<String, String> locations;

    @Column(name = "binary")
    private byte[] binaryData;

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", id)
                .append("Plate no", plateNo)
                .append("operator", operator)
                .append("locations", locations)
                .append("binarydata", binaryData)
                .toString();
    }

    @Override
    public String getUri() {
        return "taxi/taxi" + id;
    }

    @Override
    public List<String> getCollections() {
        return COLLECTIONS;
    }

    @XmlAttribute
    public String getPlateNo() {
        return plateNo;
    }
    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Map<String, String> getLocations() {
        return locations;
    }
    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

}
