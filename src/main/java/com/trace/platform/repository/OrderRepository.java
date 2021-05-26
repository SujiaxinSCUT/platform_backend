package com.trace.platform.repository;

import com.trace.platform.entity.Order;
import com.trace.platform.entity.OrderWithProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into `system_order`" +
            " (client_name, date, status, supplier_name) values (:client_name, :date, :status, :supplier_name)")
    void insert(@Param("client_name") String clientName, @Param("supplier_name") String supplierName, @Param("status")String status, @Param("date")Date date);

    @Query(nativeQuery = true, value = "select * from `system_order` where client_name = :client_name and date = :date and supplier_name = :supplier_name")
    Order findOne(@Param("client_name") String clientName, @Param("supplier_name") String supplierName, @Param("date")Date date);

    @Query(nativeQuery = true, value = "select * from `system_order` where" +
            " supplier_name = :supplier_name and status = :status",
            countQuery = "select count(*) from system_order")
    Page<Order> findBySupplierIdAndStatus(@Param("supplier_name") String supplierName, @Param("status")String status, Pageable pageable);


    @Query(nativeQuery = true, value = "select system_order.id, system_order.client_name, system_order.supplier_name, system_order.status, system_order.date " +
            "from system_order, ordered_product, product " +
            "where system_order.id = ordered_product.order_id and ordered_product.product_id = product.id " +
            "and if(:supplier_query_name != '', supplier_name = :supplier_query_name, 1 = 1) " +
            "and if(:client_query_name != '', client_name = :client_query_name, 1 = 1) " +
            "and if(:supplier_match_name != '', supplier_name like %:supplier_match_name%, 1 = 1) " +
            "and if(:client_match_name != '', client_name like %:client_match_name%, 1 = 1) " +
            "and if(:start_date != '', system_order.date > :start_date, 1 = 1 ) " +
            "and if(:end_date != '', system_order.date < :end_date, 1 = 1 ) " +
            "and if(:product_name != '', product.name like %:product_name%, 1 = 1) order by system_order.date desc",
            countQuery = "select count(*) from system_order")
    Page<Order> findDynamicPageable(@Param("supplier_query_name")String supplierQueryName, @Param("client_query_name")String clientQueryName,
                                           @Param("supplier_match_name")String supplierMatchName, @Param("client_match_name")String clientMatchName,
                                           @Param("start_date") Date startDate, @Param("end_date")Date endDate,
                                           @Param("product_name")String productName, Pageable pageable);

    @Query(nativeQuery = true, value = "select avg(price) from system_order, ordered_product " +
            "where system_order.id = ordered_product.order_id and (system_order.status = 'success' or " +
            "system_order.status = 'checking') and ordered_product.product_id = :product_id")
    Double findAvgPriceByProId(@Param("product_id")int productId);

    @Query(nativeQuery = true, value = "select * from system_order where supplier_name = :username or client_name = :username order by date desc")
    Page<Order> findByUsername(@Param("username") String username, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(*) from system_order where status = 'invalid' and" +
            " (supplier_name = :account_name or client_name = :account_name)")
    Integer findCountOfInvalidOrder(@Param("account_name") String accountName);

    @Query(nativeQuery = true, value = "select count(*) from system_order where status = 'confirming' and" +
            " supplier_name = :account_name")
    Integer findCountOfConfirmingOrder(@Param("account_name") String accountName);

    @Query(nativeQuery = true, value = "select count(*) from system_order where (status = 'confirming' or status = 'checking') and" +
            " client_name = :account_name")
    Integer findCountOfCheckingOrder(@Param("account_name") String accountName);

    @Query(nativeQuery = true, value = "select count(*) from " +
            "system_order, ordered_product " +
            "where system_order.id = ordered_product.order_id and client_name = :account_name")
    Integer findCountOfTxBatches(@Param("account_name") String accountName);

    @Query(nativeQuery = true, value = "select order_id, client_name, supplier_name, date, quantity, price from system_order, ordered_product " +
            "where system_order.id = ordered_product.order_id and (system_order.status = 'success' or " +
            "system_order.status = 'checking') and ordered_product.product_id = :product_id",
            countQuery = "select count(*) from system_order")
    Page<Map<String, Object>> findByProductIdPageable(@Param("product_id")int productId, Pageable pageable);
}
