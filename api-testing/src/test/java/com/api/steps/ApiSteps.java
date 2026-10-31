package com.api.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiSteps {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private String baseUrl;
    private HttpResponse<String> lastResponse;

    @Given("base url {string}")
    public void base_url(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @When("I send GET request to {string}")
    public void i_send_get_request_to(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();

        lastResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("I send DELETE request to {string}")
    public void i_send_delete_request_to(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .DELETE()
                .build();

        lastResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("I send POST request to {string} with json body:")
    public void i_send_post_request_to_with_json_body(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        lastResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("I send PUT request to {string} with json body:")
    public void i_send_put_request_to_with_json_body(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        lastResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("I send PATCH request to {string} with json body:")
    public void i_send_patch_request_to_with_json_body(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; charset=UTF-8")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();

        lastResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Then("response status should be {int}")
    public void response_status_should_be(Integer statusCode) {
        Assert.assertNotNull(lastResponse, "No response captured");
        Assert.assertEquals((Integer) lastResponse.statusCode(), statusCode);
    }

    @Then("response body should contain {string}")
    public void response_body_should_contain(String expected) {
        Assert.assertNotNull(lastResponse, "No response captured");
        String body = lastResponse.body();
        Assert.assertNotNull(body, "Response body is null");

        if (body.contains(expected)) {
            return;
        }

        String normalizedBody = body.replaceAll("\\s+", "");
        String normalizedExpected = expected.replaceAll("\\s+", "");
        Assert.assertTrue(normalizedBody.contains(normalizedExpected),
                "Expected text not found.\nExpected: " + expected + "\nActual: " + body);
    }
}

