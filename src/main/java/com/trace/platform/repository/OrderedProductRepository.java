package com.trace.platform.repository;

import com.trace.platform.entity.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Integer> {


}
