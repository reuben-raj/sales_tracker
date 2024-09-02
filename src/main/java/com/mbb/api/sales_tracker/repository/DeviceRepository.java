package com.mbb.api.sales_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mbb.api.sales_tracker.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

}
