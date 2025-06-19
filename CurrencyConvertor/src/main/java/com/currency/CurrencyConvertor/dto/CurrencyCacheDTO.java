package com.currency.CurrencyConvertor.dto;

import java.time.LocalDateTime;

public class CurrencyCacheDTO {

    private Long id;

    private String baseCurrency;

    private String ratesJson;

    private LocalDateTime lastUpdated;

    private LocalDateTime nextUpdate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(LocalDateTime nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getRatesJson() {
        return ratesJson;
    }

    public void setRatesJson(String ratesJson) {
        this.ratesJson = ratesJson;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
}
