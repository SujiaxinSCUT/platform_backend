package com.trace.platform.repository;

import com.trace.platform.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(nativeQuery = true, value = "select * from order where" +
            " supplier_id = :supplier_id and status = :status",
            countQuery = "select count(*) from order")
    public Page<Order> findBySupplierIdAndStatus(@Param("supplier_id") int supplierId, @Param("status")String status, Pageable pageable);
}
