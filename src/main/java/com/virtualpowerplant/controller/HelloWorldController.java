package com.virtualpowerplant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Hello World", description = "Hello World API")
public class HelloWorldController {

    @GetMapping("/hello")
    @Operation(
            summary = "Get Hello World message",
            description = "Returns a simple Hello World greeting message"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Hello World message")
    })
    public String hello() {
        return "Hello World";
    }
}