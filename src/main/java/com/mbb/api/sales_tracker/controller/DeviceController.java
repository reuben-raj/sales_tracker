package com.mbb.api.sales_tracker.controller;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mbb.api.sales_tracker.dto.BrandResponse;
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<DeviceRequest> requestEntity = new HttpEntity<>(deviceRequest, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(deviceDataUrl, HttpMethod.POST, requestEntity, String.class);
            // this API call will always redirect so we need to follow it
            if(responseEntity.getStatusCode().is3xxRedirection()){
                URI redirectUrl = responseEntity.getHeaders().getLocation();
                if(redirectUrl != null) {
                    ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> redirectResponseEntity =
                    restTemplate.exchange(redirectUrl, HttpMethod.GET, new HttpEntity<>(headers),
                            new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {});
                    return DeviceAPIResponseHandler.handleDeviceResponse(redirectResponseEntity);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch(Exception e) {
            logger.error("Error during API call: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
