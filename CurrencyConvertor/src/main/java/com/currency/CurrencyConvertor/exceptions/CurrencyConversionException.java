package com.currency.CurrencyConvertor.exceptions;


public class CurrencyConversionException extends RuntimeException{

    public  CurrencyConversionException(String message){
        super(message);
    }

    public CurrencyConversionException(String message, Throwable cause){
        super(message, cause);
    }
}
