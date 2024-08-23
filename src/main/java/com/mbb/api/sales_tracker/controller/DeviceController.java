package com.mbb.api.sales_tracker.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mbb.api.sales_tracker.dto.Brand;
import com.mbb.api.sales_tracker.dto.BrandResponse;
import com.mbb.api.sales_tracker.dto.Device;
import com.mbb.api.sales_tracker.dto.DeviceAPIResponseWrapper;
import com.mbb.api.sales_tracker.dto.DeviceListResponse;
import com.mbb.api.sales_tracker.model.DeviceRequest;
import com.mbb.api.sales_tracker.service.DeviceAPIResponseHandler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/device")
public class DeviceController {

    Logger logger = LoggerFactory.getLogger(DeviceController.class);

    private final RestTemplate restTemplate;
    private final String deviceDataUrl;

    public DeviceController(RestTemplate restTemplate, @Value("${device.datasource.url}") String deviceDataUrl) {
        this.restTemplate = restTemplate;
        this.deviceDataUrl = deviceDataUrl;
    }

    @GetMapping("/brands")
    public ResponseEntity<?> getAllBrands() {
        String url = UriComponentsBuilder.fromHttpUrl(deviceDataUrl)
            .queryParam("route", "brand-list")
            .toUriString();
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity =
            restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {});
    return DeviceAPIResponseHandler.handleBrandResponse(responseEntity);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> searchDevices(@RequestBody DeviceRequest deviceRequest) {
        logger.info("Calling search API with URL: {}", deviceDataUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<DeviceRequest> requestEntity = new HttpEntity<>(deviceRequest, headers);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity =
                restTemplate.exchange(deviceDataUrl, HttpMethod.POST, requestEntity,
                        new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {});
        return DeviceAPIResponseHandler.handleDeviceResponse(responseEntity);
    }

}
