package com.example.demo.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Currency {

    String id;
    String currencyId;
    String currencyName;
    String currencySymbol;

    public String getId() { return id;}
    public void setId(String id) { this.id = id;}

    public String getCurrencyId() { return currencyId;}
    public void setCurrencyId(String currencyId) { this.currencyId = currencyId;}

    public String getCurrencyName() { return currencyName;}
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName;}

    public String getCurrencySymbol() { return currencySymbol;}
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol;}

    public static List<Currency> create(String json) {

        List<Currency> currencyList = new ArrayList<>();

        try {
            InputStream is = new ByteArrayInputStream(json.getBytes());
            JsonReader reader = Json.createReader(is);
            JsonObject obj = reader.readObject();

            JsonObject results = obj.getJsonObject("results");

            results.keySet().forEach(keyStr->{
                JsonObject value = (JsonObject) results.get(keyStr);
                //System.out.println("key: "+ keyStr + " value: " + value);
                Currency currency = new Currency();
                currency.setCurrencyId(value.getString("currencyId"));
                currency.setCurrencyName(value.getString("currencyName"));
                currency.setCurrencySymbol(value.getString("currencySymbol"));
                currency.setId(value.getString("id"));
                currencyList.add(currency);
            });

        } catch (Exception e) {
            System.out.println(">>>>>> Currency class: unable to convert to JsonObject");
            e.printStackTrace();
        }

//        for (Currency c: currencyList){
//            System.out.println(c.currencyName);
//        }

        return currencyList;
    }
}
