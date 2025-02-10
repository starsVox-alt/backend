package com.starsvox.backend.controller;

import com.starsvox.backend.model.AudioRequest;
import com.starsvox.backend.model.AudioResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AudioController {

    private static final String API_BASE_URL = "https://apibox.erweima.ai/api/v1/generate"; // URLを修正
    private static final String API_KEY = "a598074ca3130eaff8389734fb1dab72"; // 実際のAPIキーを設定

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AudioResponse> generateAudio(@RequestBody AudioRequest audioRequest) {
        try {
            String prompt = audioRequest.getPrompt();

            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest().body(new AudioResponse(null, "prompt is required"));
            }

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost generatePost = new HttpPost(API_BASE_URL); // URLを修正
                generatePost.addHeader("Authorization", "Bearer " + API_KEY);
                generatePost.addHeader("Content-Type", "application/json");

                JSONObject generateJson = new JSONObject();
                generateJson.put("prompt", prompt);
                generateJson.put("style", "Classical"); // 固定値
                generateJson.put("title", "Peaceful Piano Meditation"); // 固定値
                generateJson.put("customMode", true); // 固定値
                generateJson.put("instrumental", false); // 固定値
                generateJson.put("model", "V3_5"); // 固定値
                generateJson.put("callBackUrl", "https://api.example.com/callback"); // 固定値

                generatePost.setEntity(new StringEntity(generateJson.toString(), "UTF-8"));

                try (CloseableHttpResponse generateResponse = httpClient.execute(generatePost)) {
                    int statusCode = generateResponse.getStatusLine().getStatusCode();
                    String responseJson = EntityUtils.toString(generateResponse.getEntity()); // レスポンス全体を取得

                    if (statusCode != 200) {
                        return ResponseEntity.status(statusCode).body(new AudioResponse(null, "failed to generate audio: " + responseJson + ", Status Code: " + statusCode)); // エラーメッセージにレスポンス全体を含める
                    }

                    JSONObject responseData = new JSONObject(responseJson);
                    String taskId = responseData.getJSONObject("data").getString("taskId"); // taskIdを取得

                    return ResponseEntity.ok(new AudioResponse(taskId, null)); // taskIdを返す

                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AudioResponse(null, "Internal Server Error: " + e.getMessage()));
        }
    }
}