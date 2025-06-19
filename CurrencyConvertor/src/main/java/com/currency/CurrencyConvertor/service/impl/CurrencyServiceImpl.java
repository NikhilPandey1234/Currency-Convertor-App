package com.currency.CurrencyConvertor.service.impl;

import com.currency.CurrencyConvertor.dto.ConversionHistoryDTO;
import com.currency.CurrencyConvertor.dto.CurrencyCacheDTO;
import com.currency.CurrencyConvertor.entity.ConversionHistory;
import com.currency.CurrencyConvertor.entity.CurrencyCache;
import com.currency.CurrencyConvertor.exceptions.CurrencyConversionException;
import com.currency.CurrencyConvertor.mappers.ModelMappers;
import com.currency.CurrencyConvertor.repo.ConversionHistoryRepository;
import com.currency.CurrencyConvertor.repo.CurrencyCacheRepository;
import com.currency.CurrencyConvertor.service.CurrencyService;
import com.currency.CurrencyConvertor.util.CurrencyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private static Logger log = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    @Value("${currency.api.key}")
    private String apiKey;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.cache.expiration.minutes:60}")
    private int cacheExpirationMinutes;

    @Autowired
    private ModelMappers mappers;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CurrencyCacheRepository currencyCacheRepository;
    private final ConversionHistoryRepository conversionHistoryRepository;

    @Autowired
    public CurrencyServiceImpl(
            CurrencyCacheRepository currencyCacheRepository,
            ConversionHistoryRepository conversionHistoryRepository) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.currencyCacheRepository = currencyCacheRepository;
        this.conversionHistoryRepository = conversionHistoryRepository;
    }

    public CurrencyResponse getExchangeRates(String baseCurrency) {
        // Check if we have a valid cached response
        LocalDateTime now = LocalDateTime.now();
        boolean hasValidCache = currencyCacheRepository
                .existsByBaseCurrencyAndNextUpdateAfter(baseCurrency, now);

        if (hasValidCache) {
            log.info("Using cached exchange rates for {}", baseCurrency);
            return getCachedRates(baseCurrency);
        } else {
            log.info("Fetching fresh exchange rates for {}", baseCurrency);
            return fetchAndCacheRates(baseCurrency);
        }
    }

    private CurrencyResponse getCachedRates(String baseCurrency) {
        return currencyCacheRepository.findByBaseCurrency(baseCurrency)
                .map(cache -> {
                    try {
                        return objectMapper.readValue(cache.getRatesJson(), CurrencyResponse.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error deserializing cached rates: {}", e.getMessage());
                        return fetchAndCacheRates(baseCurrency);
                    }
                })
                .orElseGet(() -> fetchAndCacheRates(baseCurrency));
    }

    private CurrencyResponse fetchAndCacheRates(String baseCurrency) {
        String url = apiUrl + apiKey + "/latest/" + baseCurrency;
        CurrencyResponse response = restTemplate.getForObject(url, CurrencyResponse.class);

        if (response != null && "success".equals(response.getResult())) {
            // Cache the response
            try {
                String ratesJson = objectMapper.writeValueAsString(response);

                CurrencyCacheDTO cacheDTO = currencyCacheRepository
                        .findByBaseCurrency(baseCurrency)
                        .map(mappers::currencyCacheEntityToCurrencyCacheDTO)
                        .orElse(new CurrencyCacheDTO());

                cacheDTO.setBaseCurrency(baseCurrency);
                cacheDTO.setRatesJson(ratesJson);
                cacheDTO.setLastUpdated(
                        LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(response.getTimeLastUpdateUnix()),
                                ZoneId.systemDefault()
                        )
                );
                cacheDTO.setNextUpdate(
                        LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(response.getTimeNextUpdateUnix()),
                                ZoneId.systemDefault()
                        )
                );
                CurrencyCache cache = mappers.currencyCacheDTOToCurrencyCacheEntity(cacheDTO);
                currencyCacheRepository.save(cache);
            } catch (JsonProcessingException e) {
                log.error("Error serializing rates response: {}", e.getMessage());
            }
        }

        return response;
    }

    @Scheduled(fixedRateString = "${currency.cache.expiration.minutes:60}000")
    public void refreshCaches() {
        log.info("Scheduled refresh of currency caches");
        LocalDateTime now = LocalDateTime.now();
        currencyCacheRepository.findAll().stream()
                .filter(cache -> cache.getNextUpdate().isBefore(now))
                .forEach(cache -> fetchAndCacheRates(cache.getBaseCurrency()));
    }

    public Map<String, Double> getAllCurrencies() {
        // Use USD as default for getting all available currencies
        CurrencyResponse response = getExchangeRates("USD");
        if (response != null && "success".equals(response.getResult())) {
            return new TreeMap<>(response.getConversionRates());
        }
        return Map.of();
    }

    public ConversionHistoryDTO convertCurrency(
            String fromCurrency,
            String toCurrency,
            double amount,
            HttpServletRequest request) {

        CurrencyResponse response = getExchangeRates(fromCurrency);

        if (response != null && "success".equals(response.getResult())) {
            Double rate = response.getConversionRates().get(toCurrency);

            if (rate != null) {
                BigDecimal amountBd = BigDecimal.valueOf(amount);
                BigDecimal rateBd = BigDecimal.valueOf(rate);
                BigDecimal resultBd = amountBd.multiply(rateBd)
                        .setScale(4, RoundingMode.HALF_UP);

                // Save conversion history
                ConversionHistoryDTO historyDTO = new ConversionHistoryDTO();
                historyDTO.setFromCurrency(fromCurrency);
                historyDTO.setToCurrency(toCurrency);
                historyDTO.setAmount(amountBd);
                historyDTO.setResult(resultBd);
                historyDTO.setRate(rateBd);
                historyDTO.setIpAddress(getClientIp(request));

                ConversionHistory conversionHistory = mappers.conversionHistoryDtoToConversionHistoryEntity(historyDTO);
                return mappers.conversionHistoryEntityToConversionHistoryDTO(conversionHistoryRepository.save(conversionHistory));
            }
        }

        throw new CurrencyConversionException("Failed to convert currency");
    }

    public Page<ConversionHistoryDTO> getConversionHistory(Pageable pageable) {
        Page<ConversionHistory> historyPage = conversionHistoryRepository.findAllByOrderByTimestampDesc(pageable);
        return historyPage.map(mappers::conversionHistoryEntityToConversionHistoryDTO);
    }

    public Page<ConversionHistoryDTO> getConversionHistory(
            String fromCurrency,
            String toCurrency,
            Pageable pageable) {

        Page<ConversionHistory> historyPage = conversionHistoryRepository
                .findByFromCurrencyAndToCurrencyOrderByTimestampDesc(
                        fromCurrency, toCurrency, pageable);
        return historyPage.map(mappers::conversionHistoryEntityToConversionHistoryDTO);
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // In case of multiple proxies, first address is the client's
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}

