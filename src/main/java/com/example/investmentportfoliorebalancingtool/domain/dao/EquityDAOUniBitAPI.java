package com.example.investmentportfoliorebalancingtool.domain.dao;

import com.example.investmentportfoliorebalancingtool.domain.CountryCodeAlpha2;
import com.example.investmentportfoliorebalancingtool.domain.Equity;
import com.example.investmentportfoliorebalancingtool.domain.EquitySector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Repository
public class EquityDAOUniBitAPI implements EquityDAO {
    @Override
    public Equity read(String equitySymbol) {
        // Handle NumberFormatException (BigDecimal.ValueOf)
        try {
            //return new Share(shareSymbol, BigDecimal.valueOf(getLastClosingPrice(shareSymbol)), getCompanyProfile(shareSymbol));
            double lastClosingPrice = getLastClosingPrice(equitySymbol);
            if(lastClosingPrice == -1) {
                return null;
            }

            Map<String, Object> companyProfile = getCompanyProfile(equitySymbol);
            if(companyProfile != null && companyProfile.get(equitySymbol) != null ) {
                companyProfile = (Map<String, Object>) companyProfile.get(equitySymbol);
                String sector = (String) companyProfile.get("sector");
                EquitySector equitySector = EquitySector.UNKNOWN;
                if(sector != null) {
                    equitySector = switch (sector) {
                        case "Energy" -> EquitySector.ENERGY;
                        case "Materials" -> EquitySector.MATERIALS;
                        case "Industrials" -> EquitySector.INDUSTRIALS;
                        case "Utilities" -> EquitySector.UTILITIES;
                        case "Healthcare" -> EquitySector.HEALTHCARE;
                        case "Financial Services" -> EquitySector.FINANCIALS;
                        case "Consumer Discretionary" -> EquitySector.CONSUMER_DISCRETIONARY;
                        case "Consumer Defensive" -> EquitySector.CONSUMER_STAPLES;
                        case "Technology" -> EquitySector.INFORMATION_TECHNOLOGY;
                        case "Communication Services" -> EquitySector.COMMUNICATION_SERVICES;
                        case "Real Estate" -> EquitySector.REAL_ESTATE;
                        default -> EquitySector.UNKNOWN;
                    };
                }
                return new Equity(equitySymbol,
                        BigDecimal.valueOf(lastClosingPrice),
                        equitySymbol.contains(".TO") ? CountryCodeAlpha2.CA : CountryCodeAlpha2.US,
                        equitySymbol,
                        (String) companyProfile.get("company_name"),
                        equitySector,
                        (String) companyProfile.get("industry"));
            }
            else {
                return new Equity(equitySymbol,
                        BigDecimal.valueOf(lastClosingPrice),
                        equitySymbol.contains(".TO") ? CountryCodeAlpha2.CA : CountryCodeAlpha2.US,
                        equitySymbol);
            }
        }
        catch (NumberFormatException | IOException | InterruptedException e) {
            System.out.println("Unable to fetch asset details!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private double getLastClosingPrice(String equitySymbol) throws IOException, InterruptedException {
        // Create HTTP Client to send requests and retrieve responses
        HttpClient client = HttpClient.newHttpClient();

        // Form API Request URL
        String apiRequestURL = String.format("https://api.unibit.ai/v2/stock/historical/" +
                "?tickers=%1$s" +
                "&selectedFields=close" +
                "&dataType=json" +
                "&accessKey=fUL541o7FQ-3DQHBKMEXmv_oWxOKjmZ4" , equitySymbol);

        // Build an HTTP request - GET request to API URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiRequestURL))
                .build();

        // Retrieve the HTTP response after its (response) body bytes are converted into String type -
        // using BodySubscriber<String> returned by BodyHandler<String> returned by BodyHandlers.ofString()
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println("<<<<<<<<<<<I was called>>>>>>>>>>>");

        // Downcast Object returned by response.body() to String
        String jsonResponseStr = (String) response.body();

        // Create Jackson Databind ObjectMapper object
        // "The Jackson ObjectMapper can parse JSON from a string, stream or file, and create a Java object or object graph representing the parsed JSON.
        // Parsing JSON into Java objects is also referred to as to deserialize Java objects from JSON." -
        // https://jenkov.com/tutorials/java-json/jackson-objectmapper.html
        // https://www.baeldung.com/jackson-object-mapper-tutorial
        ObjectMapper objectMapper = new ObjectMapper();

        // Type erasure - Enforcing type constraints only at compile-time and discarding element type information at runtime
        // Compiler does it to ensure type safety of our code and prevent runtime errors
        // Due to Type Erasure, Generic type information (like, <String, object>) is lost at runtime
        // To retrieve and retain the type information at runtime for Jackson to be able to deserialize to the correct type,
        // TypeReference object is created and extended (Anonymous classes)
        // https://stackoverflow.com/questions/67866342/what-is-typereference-in-java-which-is-used-while-converting-a-json-script-to-ma
        Map<String, Object> map = objectMapper.readValue(jsonResponseStr, new TypeReference<Map<String, Object>>(){});

        List<Map<String, Object>> resultData = (List<Map<String, Object>>) ((Map<String, Object>) map.get("result_data")).get(equitySymbol);
        //List<Map<String, Object>> shareData = (List<Map<String, Object>>) resultData.get(shareSymbol);

        if(resultData == null || resultData.isEmpty()) {
            return -1;
        }
        return (double) resultData.get(0).get("close");
    }

    private Map<String, Object> getCompanyProfile(String shareSymbol) throws IOException, InterruptedException {
        // Create HTTP Client to send requests and retrieve responses
        HttpClient client = HttpClient.newHttpClient();

        // Form API Request URL
        String apiRequestURL = String.format("https://api.unibit.ai/v2/company/profile/" +
                "?tickers=%1$s" +
                "&dataType=json" +
                "&accessKey=fUL541o7FQ-3DQHBKMEXmv_oWxOKjmZ4", shareSymbol);

        // Build an HTTP request - GET request to API URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiRequestURL))
                .build();

        // Retrieve the HTTP response after its (response) body bytes are converted into String type
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println("<<<<<<<<<<<I was called>>>>>>>>>>>");

        // Downcast Object returned by response.body() to String
        String jsonResponseStr = (String) response.body();

        // Create Jackson Databind ObjectMapper object
        ObjectMapper objectMapper = new ObjectMapper();

        // Create TypeReference object
        Map<String, Object> map = objectMapper.readValue(jsonResponseStr, new TypeReference<Map<String, Object>>(){});

        Map<String, Object> resultData = (Map<String, Object>) ((Map<String, Object>) map.get("result_data"));
        //Map<String, Object> shareData = (Map<String, Object>) resultData.get(shareSymbol);

        return resultData;
    }
}
