package com.mbb.api.sales_tracker.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mbb.api.sales_tracker.service.DeviceService;

@Component
public class DeviceScheduler {

    Logger logger = LoggerFactory.getLogger(DeviceScheduler.class);

    @Autowired
    private DeviceService deviceService;

    // @Scheduled(fixedRateString = "${scheduler.brands.rate}")
    @Scheduled(cron = "${scheduler.brands.expression}")
    public void updateBrands() {
        deviceService.updateBrandsFromSource();
    }

}
