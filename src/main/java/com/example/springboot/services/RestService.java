package com.example.springboot.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.Duration;
import java.util.*;

@Service
public class RestService {
    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {

        this.restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(500))
                .setReadTimeout(Duration.ofSeconds(500)).build();
    }

    public String getPostsPlainJSON() {
        String url = "https://dot.innovatrics.com/core/api/v6/actuator/info";
        return this.restTemplate.getForObject(url, String.class);
    }



    public String findTemplate(String imageAsString, String endpoint) {
        String url = "https://dot.innovatrics.com/core" + endpoint;


        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create a map for post parameters
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> image = new HashMap<>();
        data.put("data", imageAsString);
        image.put("image", data);
        image.put("template", true);
        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(image, headers);

        // send POST request
        ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);

        // check response status code
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseAsString = response.getBody();
            // toto premysliet na key:value, co ked sa zmeni vystup v api
            String template = responseAsString.substring(responseAsString.indexOf("template"),
                    responseAsString.indexOf("templateVersion"));
            template = template.substring(template.indexOf(":"), template.indexOf(","));
            template = template.substring(template.indexOf("\"") + 1, template.lastIndexOf("\""));
            return template;
        } else {
            return response.getBody();
        }
    }

    public double verifyRequest(String endpoint, String referencePhotoTemplate, String probePhotoTemplate){
        String url = "https://dot.innovatrics.com/core" + endpoint;

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> request = new HashMap<>();
        Map<String, Object> image = new HashMap<>();
        request.put("probeTemplate", probePhotoTemplate);
        request.put("referenceTemplate", referencePhotoTemplate);
        /*image.put("image", request);
        image.put("template", true);*/

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);
        String responseAsString = response.getBody();
        responseAsString = responseAsString.substring(responseAsString.indexOf(":") + 1,
                responseAsString.indexOf("}") - 1);
        double similarityScore = Double.parseDouble(responseAsString);
        return similarityScore;
    }





}
