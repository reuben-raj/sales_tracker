package com.mbb.api.sales_tracker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.mbb.api.sales_tracker.dto.Brand;
import com.mbb.api.sales_tracker.dto.BrandResponse;
import com.mbb.api.sales_tracker.dto.Device;
import com.mbb.api.sales_tracker.dto.DeviceAPIResponseWrapper;
import com.mbb.api.sales_tracker.dto.DeviceListResponse;
import com.mbb.api.sales_tracker.dto.DeviceResponse;
import com.mbb.api.sales_tracker.model.DeviceRequest;

@WebMvcTest(DeviceController.class)
@ActiveProfiles("unit")
public class DeviceControllerTest {

    private static final String DEVICE_DATA_URL = "https://script.google.com/macros/s/AKfycbxNu27V2Y2LuKUIQMK8lX1y0joB6YmG6hUwB1fNeVbgzEh22TcDGrOak03Fk3uBHmz-/exec";

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private DeviceController deviceController;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public String deviceDataUrl() {
            return DEVICE_DATA_URL;
        }
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBrands() throws Exception {
        String url = DEVICE_DATA_URL + "?route=brand-list";
        BrandResponse brandResponse = new BrandResponse();
        brandResponse.setBrandId(1);
        brandResponse.setBrandName("Nokia");
        brandResponse.setKey("nokia");

        DeviceAPIResponseWrapper<List<BrandResponse>> mockResponse = new DeviceAPIResponseWrapper<>();
        mockResponse.setStatus(200);
        mockResponse.setMessage("Success");
        mockResponse.setData(Arrays.asList(brandResponse));

        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity = 
            new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {})))
            .thenReturn(responseEntity);

        ResponseEntity<?> response = deviceController.getAllBrands();
        List<Brand> data = (List<Brand>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nokia", data.get(0).getBrandName());
    }

    @Test
    void testSearchDevices() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> initialResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("https://redirect-url.com"))
                .build();

        when(restTemplate.exchange(eq(DEVICE_DATA_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
        .thenReturn(initialResponseEntity);

        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setDeviceName("iPhone 11");
        deviceResponse.setDeviceImage("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg");
        deviceResponse.setKey("apple_iphone_11-9848");

        DeviceListResponse deviceListResponse = new DeviceListResponse();
        deviceListResponse.setDeviceList(Arrays.asList(deviceResponse));
        deviceListResponse.setTotalPage(1);

        DeviceAPIResponseWrapper<DeviceListResponse> mockRedirectResponse = new DeviceAPIResponseWrapper<>();
        mockRedirectResponse.setStatus(200);
        mockRedirectResponse.setMessage("Success");
        mockRedirectResponse.setData(deviceListResponse);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> redirectResponseEntity = 
            new ResponseEntity<>(mockRedirectResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(eq(URI.create("https://redirect-url.com")), eq(HttpMethod.GET), any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(redirectResponseEntity);
        
        ResponseEntity<?> response = deviceController.searchDevices(deviceRequest);
        List<Device> data = (List<Device>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, data.size());
        assertEquals("iPhone 11", data.get(0).getDeviceName());
        assertEquals("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg", data.get(0).getDeviceImage());
        assertEquals("apple_iphone_11-9848", data.get(0).getKey());
    }

    @Test
    void testSearchDevicesRedirect() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("https://redirect-url.com"));

        ResponseEntity<String> initialResponseEntity = new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
        
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setDeviceName("iPhone 11");
        deviceResponse.setDeviceImage("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg");
        deviceResponse.setKey("apple_iphone_11-9848");
        
        DeviceListResponse deviceListResponse = new DeviceListResponse();
        deviceListResponse.setDeviceList(Arrays.asList(deviceResponse));
        deviceListResponse.setTotalPage(1);

        DeviceAPIResponseWrapper<DeviceListResponse> redirectedResponseWrapper = new DeviceAPIResponseWrapper<>();
        redirectedResponseWrapper.setStatus(200);
        redirectedResponseWrapper.setMessage("Success");
        redirectedResponseWrapper.setData(deviceListResponse);

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> redirectedResponseEntity = 
            new ResponseEntity<>(redirectedResponseWrapper, HttpStatus.OK);

        when(restTemplate.exchange(eq(DEVICE_DATA_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
        .thenReturn(initialResponseEntity);

        when(restTemplate.exchange(eq(URI.create("https://redirect-url.com")), eq(HttpMethod.GET), any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(redirectedResponseEntity);

        ResponseEntity<?> response = deviceController.searchDevices(deviceRequest);
        List<Device> data = (List<Device>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, data.size());
        assertEquals("iPhone 11", data.get(0).getDeviceName());

        // ResponseEntity<String> responseEntityWithoutLocation = new ResponseEntity<>(HttpStatus.FOUND);
    }

    @Test
    void testSearchDevicesMissingRedirectURL() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> initiResponseEntity = ResponseEntity.status(HttpStatus.FOUND).build();

        when(restTemplate.exchange(eq(DEVICE_DATA_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
        .thenReturn(initiResponseEntity);

        ResponseEntity<?> response = deviceController.searchDevices(deviceRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSearchDevicesInternalServerError() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        when(restTemplate.exchange(eq(DEVICE_DATA_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Simulated Server Error"));

        ResponseEntity<?> response = deviceController.searchDevices(deviceRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());
    }
}
