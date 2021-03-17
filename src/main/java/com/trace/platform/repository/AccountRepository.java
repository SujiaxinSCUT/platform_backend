package com.trace.platform.repository;

import com.trace.platform.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface AccountRepository extends JpaRepository<Account, Integer> {

    public Account findByName(String name);
}
