package com.mbb.api.sales_tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrandResponse {
    @JsonProperty("brand_id")
    private int brandId;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("key")
    private String key;

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
