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

import java.util.Map;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface StockRepository extends JpaRepository<Stock, Integer> {

    @Query(nativeQuery = true, value = "select id,name,description,unit,sum from " +
            "product left join " +
            "(select product_id, sum(cast(quantity as decimal(18,2))) as sum from stock where account_id = :account_id group by product_id) as record" +
            " on record.product_id = product.id",
            countQuery = "select count(*) from stock")
    Page<Map<String, Object>> findProductInStock(@Param("account_id") int account_id, Pageable pageable);
}
