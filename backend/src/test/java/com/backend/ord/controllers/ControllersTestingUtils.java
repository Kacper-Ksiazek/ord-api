package com.backend.ord.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public final class ControllersTestingUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static <T> T getResponseBody(MvcResult result, TypeReference<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                responseType
        );
    }

    public static <T> T getResponseBody(MockHttpServletResponse response, TypeReference<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                response.getContentAsString(),
                responseType
        );
    }


    public static int getResponseStatus(ResponseEntity<?> responseEntity) {
        return responseEntity.getStatusCode().value();
    }

}
