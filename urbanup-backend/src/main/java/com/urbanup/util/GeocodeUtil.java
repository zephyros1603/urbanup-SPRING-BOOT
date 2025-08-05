package com.urbanup.util;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class GeocodeUtil {

    private static final String GEOCODE_API_URL = "https://api.example.com/geocode"; // Replace with actual API URL
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key

    public static String getGeocode(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = UriComponentsBuilder.fromHttpUrl(GEOCODE_API_URL)
                .queryParam("address", address)
                .queryParam("key", API_KEY)
                .toUriString();

        return restTemplate.getForObject(uri, String.class);
    }

    public static String getReverseGeocode(double latitude, double longitude) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = UriComponentsBuilder.fromHttpUrl(GEOCODE_API_URL + "/reverse")
                .queryParam("lat", latitude)
                .queryParam("lng", longitude)
                .queryParam("key", API_KEY)
                .toUriString();

        return restTemplate.getForObject(uri, String.class);
    }
}