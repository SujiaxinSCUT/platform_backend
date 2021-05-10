package com.trace.platform.repository;

import com.trace.platform.entity.Stock;
import com.trace.platform.resource.dto.ProductDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface StockRepository extends JpaRepository<Stock, Integer> {

    @Query(nativeQuery = true, value = "select id,name,description,unit,sum from " +
            "product left join " +
            "(select product_id, sum(cast(quantity as decimal(18,2))) as sum from stock where account_id = :account_id and status = 'free' group by product_id) as record" +
            " on record.product_id = product.id",
            countQuery = "select count(*) from stock")
    Page<Map<String, Object>> findProductInStockPageable(@Param("account_id") String account_id, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from stock where account_id = :account_id and product_id = :product_id and " +
            "status = 'free' and quantity > 0")
    List<Stock> findAllByAccountIdAndProductId(String account_id, int product_id);

    @Query(nativeQuery = true, value = "select id,name,description,unit,sum from " +
            "product left join " +
            "(select product_id, sum(cast(quantity as decimal(18,2))) as sum from stock where account_id = :account_id and status = 'free' group by product_id) as record" +
            " on record.product_id = product.id")
    List<Map<String, Object>> findAllProductsInStock(@Param("account_id") String account_id);

    @Query(nativeQuery = true, value = "select * from stock where batch_id = :batch_id")
    Stock findByBatchId(@Param("batch_id")String batchId);

    @Query(nativeQuery = true, value = "select * from stock where account_id = :account_id and product_id = :product_id")
    Page<Stock> findByAccountIdAndProductIdPageable(@Param("account_id") String accountId, @Param("product_id") int productId, Pageable pageable);

}
