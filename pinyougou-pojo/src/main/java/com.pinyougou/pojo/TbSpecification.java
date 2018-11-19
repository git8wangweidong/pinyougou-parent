package com.pinyougou.pojo;

import com.pinyougou.group.Specification;

import java.io.Serializable;

public class TbSpecification extends Specification implements Serializable {
    private Long id;

    private String specName;

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName == null ? null : specName.trim();
    }
}