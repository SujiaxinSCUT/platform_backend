package com.trace.platform.repository;

import com.trace.platform.entity.ProductMaterialRel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductMaterialRelRepository extends JpaRepository<ProductMaterialRel, Integer> {


    @Query(nativeQuery = true, value = "select * from " +
            "product_material where " +
            "product_name = :product_name " +
            "and account_name = :account_name")
    Page<ProductMaterialRel> findByProductIdPageable(@Param("product_name") String productName,
                                                     @Param("account_name")String accountName, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from " +
            "product_material where " +
            "material_name = :material_name " +
            "and account_name = :account_name")
    Page<ProductMaterialRel> findByMaterialIdPageable(@Param("material_name") String materialName,
                                                     @Param("account_name")String accountName, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from " +
            "product_material where " +
            "(product_name = :product_name or material_name = :product_name) " +
            "and account_name = :account_name")
    List<ProductMaterialRel> findByProductIdAll(@Param("product_name") String productName,
                                                @Param("account_name")String accountName);

    @Query(nativeQuery = true, value = "select count(*) from " +
            "(select id from product_material where " +
            "account_name = :account_name group by product_name, product_batch_id) " +
            "as products")
    Integer findCountOfProductMaterialBatches(@Param("account_name") String accountName);
}
