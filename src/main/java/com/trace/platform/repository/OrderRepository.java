package com.trace.platform.repository;

import com.trace.platform.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into `order`" +
            " (client_name, date, status, supplier_name) values (:client_name, :date, :status, :supplier_name)",
    countQuery = "select count(*) from order")
    void insert(@Param("client_name") String clientName, @Param("supplier_name") String supplierName, @Param("status")String status, @Param("date")Date date);

    @Query(nativeQuery = true, value = "select * from `order` where client_name = :client_name and date = :date and supplier_name = :supplier_name",
            countQuery = "select count(*) from order")
    Order findOne(@Param("client_name") String clientName, @Param("supplier_name") String supplierName, @Param("date")Date date);

    @Query(nativeQuery = true, value = "select * from order where" +
            " supplier_name = :supplier_name and status = :status",
            countQuery = "select count(*) from order")
    Page<Order> findBySupplierIdAndStatus(@Param("supplier_name") String supplierName, @Param("status")String status, Pageable pageable);


    @Query(nativeQuery = true, value = "select `order`.id, `order`.client_name, `order`.supplier_name, `order`.status, `order`.date " +
            "from `order`, ordered_product, product " +
            "where `order`.id = ordered_product.order_id and ordered_product.product_id = product.id " +
            "and if(:supplier_query_name != null and :supplier_query_name != '', supplier_name = :supplier_query_name, 1 = 1) " +
            "and if(:client_query_name != null and :client_query_name != '', client_name = :client_query_name, 1 = 1) " +
            "and if(:supplier_match_name != null and :supplier_match_name != '', supplier_name like %:supplier_match_name%, 1 = 1) " +
            "and if(:client_match_name != null and :client_match_name != '', client_name like %:client_match_name%, 1 = 1) " +
            "and if(:start_date != null and :end_date != null, date between :start_date and :end_date, 1 = 1 ) " +
            "and if(:start_date != null and :end_date = null, date > :start_date, 1 = 1 ) " +
            "and if(:start_date = null and :end_date != null, date < :end_date, 1 = 1 ) " +
            "and if(:product_name != null and :product_name != '', product_name like %:product_name%, 1 = 1); ",
            countQuery = "select count(*) from order")
    Page<Order> findDynamicPageable(@Param("supplier_query_name")String supplierQueryName, @Param("client_query_name")String clientQueryName,
                                           @Param("supplier_match_name")String supplierMatchName, @Param("client_match_name")String clientMatchName,
                                           @Param("start_date") Date startDate, @Param("end_date")Date endDate,
                                           @Param("product_name")String productName, Pageable pageable);
}
