package moe.mcg.mcpanel.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class BaiduTranslationApi {

    private static final String API_URL = "https://fanyi-api.baidu.com/ait/api/aiTextTranslate";

    public static CompletableFuture<String> sendTranslationRequestAsync(String apiKey, String appId, String sourceLang, String targetLang, String textToTranslate) {

        String formData = "appid=" + appId + "&from=" + sourceLang + "&to=" + targetLang + "&q=" + textToTranslate;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(formData, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(BaiduTranslationApi::handleResponse);
    }

    private static String handleResponse(HttpResponse<String> response) {
        System.out.println("Response Status Code: " + response.statusCode());

        HttpHeaders headers = response.headers();
        Map<String, List<String>> headerMap = headers.map();
        System.out.println("Response Headers: " + headerMap);

        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);

        if (responseBody.contains("trans_result")) {
            return responseBody;
        } else {
            return "Error: " + responseBody;
        }
    }
}
