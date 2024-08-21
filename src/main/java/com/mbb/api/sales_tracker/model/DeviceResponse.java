package com.mbb.api.sales_tracker.model;

import java.util.List;

public class DeviceResponse {
    private int status;
    
    private String message;

    private DeviceData data;

    public static class DeviceData {
        private List<Device> device_list;

        private int total_page;

        public static class Device {
            private String device_name;

            private String device_image;

            private String key;

            public String getDevice_name() {
                return device_name;
            }

            public void setDevice_name(String device_name) {
                this.device_name = device_name;
            }

            public String getDevice_image() {
                return device_image;
            }

            public void setDevice_image(String device_image) {
                this.device_image = device_image;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
            
        }

        public List<Device> getDevice_list() {
            return device_list;
        }

        public void setDevice_list(List<Device> device_list) {
            this.device_list = device_list;
        }

        public int getTotal_page() {
            return total_page;
        }

        public void setTotal_page(int total_page) {
            this.total_page = total_page;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DeviceData getData() {
        return data;
    }

    public void setData(DeviceData data) {
        this.data = data;
    }

}
