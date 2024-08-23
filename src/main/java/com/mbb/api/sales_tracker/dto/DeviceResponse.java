package com.mbb.api.sales_tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceResponse {
    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("device_image")
    private String deviceImage;

    @JsonProperty("key")
    private String key;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(String deviceImage) {
        this.deviceImage = deviceImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
