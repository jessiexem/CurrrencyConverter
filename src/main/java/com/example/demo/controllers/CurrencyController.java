package com.example.demo.controllers;

import com.example.demo.model.Currency;
import com.example.demo.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Controller
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @GetMapping
    public String showHomePage(Model model) {
        Optional<List<Currency>> opt = currencyService.getCurrencies();

        if(opt.isEmpty()){
            return "404";
        }

        List<Currency> currencyList = opt.get();

        model.addAttribute("currList",currencyList);
        return "index";
    }

    @PostMapping(path = "/convert", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getConversionRate(@RequestBody MultiValueMap<String,String> form, Model model) {
        System.out.println("In Controller getConversionRate method");
        String [] currIdNameFrom = form.getFirst("currencyFrom")
                                        .split(",");
        String [] currIdNameTo = form.getFirst("currencyTo")
                                        .split(",");


        String currIdFrom = currIdNameFrom[0];
        String currNameFrom = currIdNameFrom[1];
        String currSymbolFrom = currIdNameFrom[2];

        String currIdTo = currIdNameTo[0];
        String currNameTo = currIdNameTo[1];
        String currSymbolTo = currIdNameTo[2];

        double amount = Double.parseDouble(form.getFirst("amount"));

        System.out.println(currNameFrom+currNameTo+amount);

        double result = currencyService.getConversionRate(currIdFrom,currIdTo,amount);

        model.addAttribute("currNameFrom",currNameFrom);
        model.addAttribute("currSymbolFrom",currSymbolFrom);
        model.addAttribute("currNameTo",currNameTo);
        model.addAttribute("currSymbolTo",currSymbolTo);
        model.addAttribute("currFromAmt",amount);
        model.addAttribute("currToAmt",result);
        return "displayConversion";
    }
}
