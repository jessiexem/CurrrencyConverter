package com.example.demo.service;

import com.example.demo.model.Currency;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    @Value("${CURR_CONV_KEY}")
    private String apiKey;

    //https://free.currconv.com/api/v7/countries?apiKey=xxx"

    private static final String SUPPORTED_CURR_URL = "https://free.currconv.com/api/v7/countries";
    private static final String CONV_RATE_URL = "https://free.currconv.com/api/v7/convert";

//    @PostConstruct
//    public void init() {
//        apiKey = System.getenv("CURR_CONV_KEY");
//    }

    public Optional<List<Currency>> getCurrencies() {

        String url = UriComponentsBuilder
                .fromUriString(SUPPORTED_CURR_URL)
                .queryParam("apiKey",apiKey)
                .toUriString();

        RequestEntity req = RequestEntity.get(url).build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req,String.class);

        System.out.println(">>>>>>> CurrSevice: "+resp.getBody());

        try {
            List<Currency> currency = Currency.create(resp.getBody())
                    //to sort by alphabetical order
                    .stream()
                    .sorted((o1,o2)-> o1.getCurrencyName().compareTo(o2.getCurrencyName()))
                    .toList();

            System.out.println("-----CurrSvc: currency list created");
            return Optional.of(currency);
        } catch (Exception e) {
            System.out.println(">>>> CurrSvc: Error creating currency");
        }

        return Optional.empty();
    }

    public double getConversionRate(String currFrom, String currTo, double amount) {

        //https://free.currconv.com/api/v7/convert?q=SGD_JPY&compact=ultra&apiKey=abc123

        String query = currFrom+"_"+currTo;

        String url = UriComponentsBuilder
                .fromUriString(CONV_RATE_URL)
                .queryParam("q",query)
                .queryParam("compact","ultra")
                .queryParam("apiKey",apiKey)
                .toUriString();

        RequestEntity req = RequestEntity.get(url).build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req,String.class);

        System.out.println(">>>> In Svc getConversionRate: "+resp.getBody());

        double result=0.00;
        try {
            InputStream is = new ByteArrayInputStream(resp.getBody().getBytes());
            JsonReader reader = Json.createReader(is);
            JsonObject obj = reader.readObject();

            JsonNumber jsonRate = obj.getJsonNumber(query);
            double rate = jsonRate.doubleValue();
            System.out.println("conversion rate: "+rate);

            result = calculateConversion(amount,rate);
        } catch (Exception e) {
            System.out.println(">>>>>> CurrencySvc class: unable to convert to JsonObject");
            e.printStackTrace();
        }

        return result;
    }

    public double calculateConversion(double amount, double rate) {
        double result = amount * rate;
        System.out.println("calculateConversion result: "+result);
        return result;
    }

}
