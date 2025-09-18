package com.virtualpowerplant.constant;

import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Constant {
    public static final RestTemplate restTemplate = new RestTemplate();
    public static final ObjectMapper objectMapper = new ObjectMapper();

}
