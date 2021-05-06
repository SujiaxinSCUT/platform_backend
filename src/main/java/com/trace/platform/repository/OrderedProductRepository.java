package com.trace.platform.repository;

import com.trace.platform.entity.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Integer> {

    @Query(nativeQuery = true, value = " select * from " +
            "(select product_id, quantity, price from" +
            " ordered_product where order_id = :order_id) as ordered" +
            " left join product on ordered.product_id = product.id;",
    countQuery = "select count(*) from ordered_product;")
    public List<Map<String, Object>> findOrderedProductAll(@Param("order_id")int orderId);

    @Query(nativeQuery = true, value = "select * from " +
            "ordered_product where product_id = :pro_id and order_id = :order_id;")
    OrderedProduct findByProIdAndOrderId(@Param("pro_id") int proId, @Param("order_id")int orderId);
}
