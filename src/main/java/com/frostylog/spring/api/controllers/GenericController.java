package com.frostylog.spring.api.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/v1/")
@RestController
public class GenericController {

    @CrossOrigin
    @RequestMapping(value = "/ups/{trackingNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public String getOneFromUPS(@RequestParam final Optional<String> pParam, @PathVariable final String pTrackingNumber,
            @RequestHeader final Map<String, String> pHeaders) throws IOException, InterruptedException {

        return getResponseFromUPS(pTrackingNumber, pParam, pHeaders);
    }

    @CrossOrigin
    @RequestMapping(value = "/ups/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<String> getManyFromUPS(@RequestBody final List<String> pTrackingNumbers,
            @RequestParam final Optional<String> pParam, @RequestHeader final Map<String, String> pHeaders)
            throws IOException, InterruptedException {
        List<String> responseList = new ArrayList<>();
        for (String trackingNumber : pTrackingNumbers) {
            responseList.add(getResponseFromUPS(trackingNumber, pParam, pHeaders));
        }
        return responseList;
    }

    private String getResponseFromUPS(String pTrackingNumber, final Optional<String> pParam,
            Map<String, String> pHeaders) throws IOException, InterruptedException {
        HttpResponse<String> response;

        if (pHeaders.containsKey("accesslicensenumber")) {
            String strUrl = "https://onlinetools.ups.com/track/v1/details/" + pTrackingNumber + "?locale=en_US";
            String accessLicenseNumber = pHeaders.get("accesslicensenumber");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(strUrl))
                    .setHeader("Content-Type", "application/json").setHeader("Accept", "*/*")
                    .setHeader("AccessLicenseNumber", accessLicenseNumber).build();
            response = client.send(request, BodyHandlers.ofString());
        } else {
            throw new RuntimeException("Header does not include AccessLicenseNumber");
        }
        return response.body();
    }

}