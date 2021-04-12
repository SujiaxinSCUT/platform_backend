package com.trace.platform.repository;

import com.trace.platform.entity.FabricOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FabricOrderRepository extends JpaRepository<FabricOrder, Integer> {
}
