package com.currency.CurrencyConvertor.service;

import com.currency.CurrencyConvertor.dto.ConversionHistoryDTO;
import com.currency.CurrencyConvertor.util.CurrencyResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CurrencyService {

    CurrencyResponse getExchangeRates(String baseCurrency);

    Map<String, Double> getAllCurrencies();

    ConversionHistoryDTO convertCurrency(
            String fromCurrency,
            String toCurrency,
            double amount,
            HttpServletRequest request);

    Page<ConversionHistoryDTO> getConversionHistory(Pageable pageable);

    Page<ConversionHistoryDTO> getConversionHistory(
            String fromCurrency,
            String toCurrency,
            Pageable pageable);
}
