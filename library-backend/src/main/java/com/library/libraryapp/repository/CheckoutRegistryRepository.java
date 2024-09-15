package com.library.libraryapp.repository;

import com.library.libraryapp.entity.CheckoutRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRegistryRepository extends JpaRepository<CheckoutRegister, Long> {
}
