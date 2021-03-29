package com.trace.platform.repository;

import com.trace.platform.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface ProductRepository extends JpaRepository<Product, Integer> {

    public Product findByName(String name);

    public Page<Product> findAll(Pageable pageable);

}
