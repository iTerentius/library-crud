package com.library.libraryapp.repository;

import com.library.libraryapp.entity.CheckoutRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutRegistryRepository extends JpaRepository<CheckoutRegister, Long> {
}
