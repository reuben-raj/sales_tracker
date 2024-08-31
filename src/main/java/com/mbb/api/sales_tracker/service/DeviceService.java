package com.mbb.api.sales_tracker.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mbb.api.sales_tracker.dto.BrandResponse;
import com.mbb.api.sales_tracker.dto.DeviceAPIResponseWrapper;
import com.mbb.api.sales_tracker.model.Brand;
import com.mbb.api.sales_tracker.repository.BrandRepository;

@Service
public class DeviceService {

    Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Value("${device.datasource.url}")
    private String deviceDataUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getBrands() {
        logger.info("Reached DeviceService getBrands");
        List<Brand> brands = new ArrayList<>();

        return brands;
    }

    public List<Brand> getBrandsFromSource() {
        List<Brand> brands = new ArrayList<>();

        String url = UriComponentsBuilder.fromHttpUrl(deviceDataUrl)
            .queryParam("route", "brand-list")
            .toUriString();
        ResponseEntity<DeviceAPIResponseWrapper<List<BrandResponse>>> responseEntity =
            restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DeviceAPIResponseWrapper<List<BrandResponse>>>() {});
        
        List<BrandResponse> brandResponses = responseEntity.getBody().getData();
        logger.info("getBrandsFromSource brandResponses size "+brandResponses.size());

        for(BrandResponse brandResponse : brandResponses) {
            if(StringUtils.hasLength(brandResponse.getBrandName()) 
                && StringUtils.hasLength(brandResponse.getKey())) {
                Brand brand = new Brand();
                brand.setId(brandResponse.getBrandId());
                brand.setBrandName(brandResponse.getBrandName());
                brand.setKey(brandResponse.getKey());
                brand.setCreatedBy((long) 1);
                brand.setUpdatedBy((long) 1);
                brands.add(brand);
            }
        }

        return brands;
    }

    public List<Brand> updateBrandsFromSource() {
        List<Brand> brands = new ArrayList<>();
        brands = getBrandsFromSource();

        brandRepository.saveAll(brands);

        return brands;
    }

}
