package com.currency.CurrencyConvertor.repo;

import com.currency.CurrencyConvertor.entity.ConversionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long> {

    Page<ConversionHistory> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<ConversionHistory> findByFromCurrencyAndToCurrencyOrderByTimestampDesc(
            String fromCurrency, String toCurrency, Pageable pageable);
}
