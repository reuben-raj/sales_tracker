package com.mbb.api.sales_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mbb.api.sales_tracker.dto.Brand;
import com.mbb.api.sales_tracker.dto.BrandResponse;
import com.mbb.api.sales_tracker.dto.Device;
import com.mbb.api.sales_tracker.dto.DeviceAPIResponseWrapper;
import com.mbb.api.sales_tracker.dto.DeviceListResponse;

public class DeviceAPIResponseHandler {

    public static ResponseEntity<?> handleBrandResponse(ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity) {
        DeviceAPIResponseWrapper<List<BrandResponse>> responseBody = responseEntity.getBody();
        if (responseBody == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to retrieve data");
        }

        if (responseBody.getStatus() != 200) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error from third-party service");
        }

        List<BrandResponse> brandResponses = responseBody.getData();
        List<Brand> brands = brandResponses.stream()
                .map(brandResponse -> {
                    Brand brand = new Brand();
                    brand.setBrandId(brandResponse.getBrandId());
                    brand.setBrandName(brandResponse.getBrandName());
                    brand.setKey(brandResponse.getKey());
                    return brand;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(brands);
    }

    public static ResponseEntity<?> handleDeviceResponse(ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity) {
        DeviceAPIResponseWrapper<DeviceListResponse> responseBody = responseEntity.getBody();
        if (responseBody == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to retrieve data");
        }

        if (responseBody.getStatus() != 200) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error from third-party service");
        }

        DeviceListResponse deviceListResponse = responseBody.getData();
        List<Device> devices = deviceListResponse.getDevices();

        return ResponseEntity.ok(devices);
    }

}
