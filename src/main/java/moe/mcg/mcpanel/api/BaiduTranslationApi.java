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

import static moe.mcg.mcpanel.Main.LOGGER;

/**
 * 百度翻译 API 客户端类。
 * <p>
 * 该类封装了与百度翻译 API 进行交互的方法，包括异步发送翻译请求并处理响应。通过该类，可以将源文本翻译为目标语言。
 * 使用时，需要提供有效的 API Key 和 App ID，并指定源语言和目标语言。
 * </p>
 */
public class BaiduTranslationApi {

    // 百度翻译 API URL
    private static final String API_URL = "https://fanyi-api.baidu.com/ait/api/aiTextTranslate";

    /**
     * 发送翻译请求到百度翻译 API，返回一个包含翻译结果的 CompletableFuture。
     * <p>
     * 此方法异步地向百度翻译 API 发送请求，指定源语言、目标语言和待翻译的文本，
     * 然后返回一个 {@link CompletableFuture} 对象，表示翻译结果的异步响应。
     * </p>
     *
     * @param apiKey       百度 API 的密钥
     * @param appId        百度 API 的应用 ID
     * @param sourceLang   源语言的语言代码（例如：zh，en）
     * @param targetLang   目标语言的语言代码（例如：en，zh）
     * @param textToTranslate 待翻译的文本内容
     * @return 返回一个包含翻译结果的 CompletableFuture 对象
     */
    public static CompletableFuture<String> sendTranslationRequestAsync(String apiKey, String appId, String sourceLang, String targetLang, String textToTranslate) {

        // 构建请求的表单数据
        String formData = "appid=" + appId + "&from=" + sourceLang + "&to=" + targetLang + "&q=" + textToTranslate;

        // 创建 HTTP 客户端
        HttpClient client = HttpClient.newHttpClient();

        // 构建 HTTP 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")  // 设置内容类型为表单数据
                .header("Authorization", "Bearer " + apiKey)  // 设置授权信息
                .POST(HttpRequest.BodyPublishers.ofString(formData, StandardCharsets.UTF_8))  // 设置请求体为表单数据
                .build();

        // 发送异步请求，并在响应到达时处理结果
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(BaiduTranslationApi::handleResponse);
    }

    /**
     * 处理翻译 API 的响应。
     * <p>
     * 该方法将分析 API 响应的 JSON 内容，并根据是否包含翻译结果进行处理。
     * 如果响应包含 "trans_result" 字段，则返回翻译结果；否则返回错误信息。
     * </p>
     *
     * @param response API 响应
     * @return 翻译结果的字符串，或者错误信息
     */
    private static String handleResponse(HttpResponse<String> response) {
        // 打印响应体内容
        LOGGER.info("Response Body: {}", response.body());

        // 打印响应头信息
        HttpHeaders headers = response.headers();
        Map<String, List<String>> headerMap = headers.map();
        LOGGER.info("Response Headers: {}", headerMap);

        // 获取响应体内容
        String responseBody = response.body();
        LOGGER.info("Response Body: {}", responseBody);

        if (responseBody.contains("trans_result")) {
            return responseBody;
        } else {
            return "Error: " + responseBody;
        }
    }
}
