package com.caesaryh.comp2005fe.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.caesaryh.comp2005fe.utils.models.Patient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MaternityApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 通用GET请求方法
    private static CompletableFuture<String> asyncGetRequest(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        throw new ApiException("API request failed: " + response.statusCode());
                    }
                });
    }

    // 具体API方法
    public static CompletableFuture<List<Patient>> getNeverDischargedPatients() {
        return asyncGetRequest("f1")
                .thenApply(MaternityApiClient::parsePatientList);
    }

    public static CompletableFuture<List<Patient>> getReadmittedWithin7Days() {
        return asyncGetRequest("f2")
                .thenApply(MaternityApiClient::parsePatientList);
    }

    public static CompletableFuture<String> getBusiestMonth() {
        return asyncGetRequest("f3");
    }

    public static CompletableFuture<List<Patient>> getMultiStaffPatients() {
        return asyncGetRequest("f4")
                .thenApply(MaternityApiClient::parsePatientList);
    }

    // 解析JSON通用方法
    private static List<Patient> parsePatientList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println("error parsing json: " + json + "Because of " + e);
            throw new ApiException("JSON resolution failure", e);
        }
    }

    // 自定义异常类
    public static class ApiException extends RuntimeException {
        public ApiException(String message) {
            super(message);
        }

        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
