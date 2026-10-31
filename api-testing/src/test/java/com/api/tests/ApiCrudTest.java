package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiCrudTest {

    private HttpClient client;
    private final String baseUrl = "https://jsonplaceholder.typicode.com";

    @BeforeClass
    public void setup() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    private HttpResponse<String> sendJson(String method, String path, String jsonBody) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json");

        if (jsonBody == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json; charset=UTF-8");
            builder.method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
        }

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void get_shouldReturn200AndBody() throws Exception {
        HttpResponse<String> res = sendJson("GET", "/posts/1", null);
        Assert.assertEquals(res.statusCode(), 200);
        Assert.assertNotNull(res.body());
        Assert.assertTrue(res.body().contains("\"id\": 1"));
    }

    @Test
    public void post_shouldReturn201AndCreatedBody() throws Exception {
        String payload = "{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}";
        HttpResponse<String> res = sendJson("POST", "/posts", payload);
        Assert.assertEquals(res.statusCode(), 201);
        Assert.assertNotNull(res.body());
        Assert.assertTrue(res.body().contains("\"title\": \"foo\"") || res.body().contains("\"title\":\"foo\""));
    }

    @Test
    public void put_shouldReturn200AndUpdatedBody() throws Exception {
        String payload = "{\"id\":1,\"title\":\"updated\",\"body\":\"updated body\",\"userId\":1}";
        HttpResponse<String> res = sendJson("PUT", "/posts/1", payload);
        Assert.assertEquals(res.statusCode(), 200);
        Assert.assertNotNull(res.body());
        Assert.assertTrue(res.body().contains("\"id\": 1") || res.body().contains("\"id\":1"));
        Assert.assertTrue(res.body().contains("\"title\": \"updated\"") || res.body().contains("\"title\":\"updated\""));
    }

    @Test
    public void patch_shouldReturn200AndPatchedBody() throws Exception {
        String payload = "{\"title\":\"patched\"}";
        HttpResponse<String> res = sendJson("PATCH", "/posts/1", payload);
        Assert.assertEquals(res.statusCode(), 200);
        Assert.assertNotNull(res.body());
        Assert.assertTrue(res.body().contains("\"title\": \"patched\"") || res.body().contains("\"title\":\"patched\""));
    }

    @Test
    public void delete_shouldReturn200() throws Exception {
        HttpResponse<String> res = sendJson("DELETE", "/posts/1", null);
        Assert.assertEquals(res.statusCode(), 200);
    }
}

