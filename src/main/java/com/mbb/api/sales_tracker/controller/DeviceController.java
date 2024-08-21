package com.mbb.api.sales_tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import com.mbb.api.sales_tracker.model.BrandResponse;
import com.mbb.api.sales_tracker.model.DeviceRequest;
import com.mbb.api.sales_tracker.model.DeviceResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/device")
public class DeviceController {

    Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Value("${device.brands.url}")
    private String brandsUrl;
    
    @Value("${device.search.url}")
    private String searchUrl;

    private final RestTemplate restTemplate;

    public DeviceController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/brands")
    public ResponseEntity<?> getAllBrands() {
        String url = brandsUrl;
        BrandResponse response = restTemplate.getForObject(url, BrandResponse.class);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> searchDevices(@RequestBody DeviceRequest deviceRequest) {
        logger.info("Calling search API with URL: {}", searchUrl);
        DeviceResponse response = null;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<DeviceRequest> requestEntity = new HttpEntity<>(deviceRequest, headers);

            ResponseEntity<DeviceResponse> apiResponse = restTemplate.exchange(searchUrl, HttpMethod.POST, requestEntity, DeviceResponse.class);
            if (apiResponse.getStatusCode().is3xxRedirection()) {
                String redirectUrl = apiResponse.getHeaders().getLocation().toString();
                logger.info("Redirecting to: {}", redirectUrl);
                apiResponse = restTemplate.exchange(redirectUrl, HttpMethod.POST, requestEntity, DeviceResponse.class);
            }
            response = apiResponse.getBody();
            logger.info("Received response: {}", response);
            return ResponseEntity.ok(response);
        } catch (UnknownContentTypeException e) {
            logger.error("Unexpected content type: {}", e.getContentType());
            logger.error("Response body: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected response type from API.");
        } catch (Exception e) {
            logger.error("API call failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API call failed.");
        }
    }

}
