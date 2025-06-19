package com.currency.CurrencyConvertor.controller;

import com.currency.CurrencyConvertor.dto.ConversionHistoryDTO;
import com.currency.CurrencyConvertor.service.CurrencyService;
import com.currency.CurrencyConvertor.util.ConversionRequest;
import com.currency.CurrencyConvertor.service.impl.CurrencyServiceImpl;
import com.currency.CurrencyConvertor.util.CurrencyResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            // Get all available currencies
            Map<String, Double> currencies = currencyService.getAllCurrencies();

            // Get latest rates for USD as default
            CurrencyResponse response = currencyService.getExchangeRates("USD");

            model.addAttribute("currencies", currencies.keySet());
            model.addAttribute("conversionRequest", new ConversionRequest());
            model.addAttribute("lastUpdate", response.getTimeLastUpdateUtc());
            model.addAttribute("nextUpdate", response.getTimeNextUpdateUtc());

            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch currency data: " + e.getMessage());
            model.addAttribute("conversionRequest", new ConversionRequest());

            return "index";
        }
    }

    @PostMapping("/convert")
    public String convert(
            @ModelAttribute ConversionRequest request,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpRequest) {
        try {
            ConversionHistoryDTO history = currencyService.convertCurrency(
                    request.getFromCurrency(),
                    request.getToCurrency(),
                    request.getAmount(),
                    httpRequest
            );

            CurrencyResponse response = currencyService.getExchangeRates(request.getFromCurrency());

            model.addAttribute("result", history.getResult().toString());
            model.addAttribute("fromCurrency", history.getFromCurrency());
            model.addAttribute("toCurrency", history.getToCurrency());
            model.addAttribute("amount", history.getAmount().toString());
            model.addAttribute("rate", history.getRate().toString());
            model.addAttribute("lastUpdate", response.getTimeLastUpdateUtc());

            // Get all currencies for the form
            Map<String, Double> currencies = currencyService.getAllCurrencies();
            model.addAttribute("currencies", currencies.keySet());
            model.addAttribute("conversionRequest", new ConversionRequest());

            return "result";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Conversion failed: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/history")
    public String history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {

        try {
            Page<ConversionHistoryDTO> historyPage;

            if (from != null && to != null) {
                historyPage = currencyService.getConversionHistory(
                        from, to, PageRequest.of(page, size));
                model.addAttribute("filtering", true);
                model.addAttribute("fromFilter", from);
                model.addAttribute("toFilter", to);
            } else {
                historyPage = currencyService.getConversionHistory(PageRequest.of(page, size));
                model.addAttribute("filtering", false);
            }

            model.addAttribute("history", historyPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", historyPage.getTotalPages());
            model.addAttribute("totalItems", historyPage.getTotalElements());

            // Get all currencies for the filters
            Map<String, Double> currencies = currencyService.getAllCurrencies();
            model.addAttribute("currencies", currencies.keySet());

            return "history";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch history: " + e.getMessage());
            return "history";
        }
    }

    /*@GetMapping("/index")
    public String showLoginPage() {
        return "index";
    }

    @GetMapping("/signup.html")
    public String showSignupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String showSignupPage() {
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }*/

    @GetMapping("/signup")
    public String showSingupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}

