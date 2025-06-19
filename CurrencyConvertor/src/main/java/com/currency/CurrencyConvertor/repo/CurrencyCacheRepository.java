package com.currency.CurrencyConvertor.repo;

import com.currency.CurrencyConvertor.entity.CurrencyCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CurrencyCacheRepository extends JpaRepository<CurrencyCache, Long> {

    Optional<CurrencyCache> findByBaseCurrency(String baseCurrency);

    boolean existsByBaseCurrencyAndNextUpdateAfter(String baseCurrency, LocalDateTime now);

}
