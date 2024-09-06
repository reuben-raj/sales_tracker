package com.mbb.api.sales_tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.mbb.api.sales_tracker.dto.DeviceAPIResponseWrapper;
import com.mbb.api.sales_tracker.dto.DeviceListResponse;
import com.mbb.api.sales_tracker.dto.DeviceRequest;
import com.mbb.api.sales_tracker.dto.DeviceResponse;
import com.mbb.api.sales_tracker.model.Brand;
import com.mbb.api.sales_tracker.model.Device;
import com.mbb.api.sales_tracker.repository.BrandRepository;
import com.mbb.api.sales_tracker.repository.DeviceRepository;

@ActiveProfiles("unit")
public class DeviceServiceTest {

    private static final String DEVICE_DATA_URL = "https://script.google.com/macros/s/AKfycbxNu27V2Y2LuKUIQMK8lX1y0joB6YmG6hUwB1fNeVbgzEh22TcDGrOak03Fk3uBHmz-/exec";
    private static final String DEVICE_REDIRECT_URL = "https://redirect-url.com";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Spy
    @InjectMocks
    private DeviceService deviceService;

    private List<Brand> brands;
    private List<Device> devices;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        deviceService = new DeviceService(DEVICE_DATA_URL, restTemplate);

        Field brandRepoField = DeviceService.class.getDeclaredField("brandRepository");
        brandRepoField.setAccessible(true);
        brandRepoField.set(deviceService, brandRepository);

        Field deviceRepoField = DeviceService.class.getDeclaredField("deviceRepository");
        deviceRepoField.setAccessible(true);
        deviceRepoField.set(deviceService, deviceRepository);

        Brand brand1 = new Brand();
        brand1.setId(1);
        brand1.setBrandName("Nokia");
        brand1.setKey("nokia");

        Brand brand2 = new Brand();
        brand2.setId(2);
        brand2.setBrandName("iPhone");
        brand2.setKey("iphone");

        brands = List.of(brand1, brand2);

        Device device1 = new Device();
        device1.setId((long) 1);
        device1.setDeviceName("Nokia 5");
        device1.setDeviceType("nokia5");

        Device device2 = new Device();
        device2.setId((long) 2);
        device2.setDeviceName("iPhone 11");
        device2.setDeviceType("iphone-11");

        devices = List.of(device1, device2);
    }

    @Test
    void testGetBrands() {
        when(brandRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(brands));

        Page<Brand> result = deviceService.getBrands(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("Nokia", result.getContent().get(0).getBrandName());
    }
    
    @Test
    void testGetDevices() {
        when(deviceRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(devices));

        Page<Device> result = deviceService.getDevices(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("Nokia 5", result.getContent().get(0).getDeviceName());
    }

    @Test
    void testGetDevicesFromSource() {

    }

    @Test
    void testUpdateBrandsFromSource() {

    }

    @Test
    void testUpdateDevicesFromSource() {

    }

    @Test
    void testSearchDevices() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setBrand_id(1);
        deviceRequest.setRoute("device-list-by-brand");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> redirectResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(DEVICE_REDIRECT_URL))
                .build();

        when(restTemplate.exchange(
            eq(DEVICE_DATA_URL), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(String.class)))
        .thenReturn(redirectResponseEntity);

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

        ResponseEntity<DeviceAPIResponseWrapper<DeviceListResponse>> responseEntity = 
            new ResponseEntity<>(mockRedirectResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(URI.create(DEVICE_REDIRECT_URL)), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(new ParameterizedTypeReference<DeviceAPIResponseWrapper<DeviceListResponse>>() {})))
        .thenReturn(responseEntity);
        
        List<Device> data = deviceService.searchDevices(deviceRequest);

        assertEquals(1, data.size());
        assertEquals("iPhone 11", data.get(0).getDeviceName());
        assertEquals("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg", data.get(0).getDeviceImageUrl());
        assertEquals("apple_iphone_11-9848", data.get(0).getKey());
    }
}
