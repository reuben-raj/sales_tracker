package com.mbb.api.sales_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mbb.api.sales_tracker.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

}
