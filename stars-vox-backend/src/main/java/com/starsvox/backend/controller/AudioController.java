package com.starsvox.backend.controller;

import com.starsvox.backend.model.AudioRequest;
import com.starsvox.backend.model.AudioResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

    private static final String API_BASE_URL = "https://api.suno.ai/api/v1";
    private static final String API_KEY = "a598074ca3130eaff8389734fb1dab72"; // 実際のAPIキーを設定

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AudioResponse> generateAudio(@RequestBody AudioRequest audioRequest) {
        try {
            String prompt = audioRequest.getPrompt();

            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest().body(new AudioResponse(null, "prompt is required"));
            }

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost generatePost = new HttpPost(API_BASE_URL + "/generate");
                generatePost.addHeader("Authorization", "Bearer " + API_KEY);
                generatePost.addHeader("Content-Type", "application/json");

                JSONObject generateJson = new JSONObject();
                generateJson.put("prompt", prompt);
                // 必要であれば、他のパラメータ(楽器など)も追加
                // 例: generateJson.put("instrument", "piano");

                generatePost.setEntity(new StringEntity(generateJson.toString(), "UTF-8")); // UTF-8エンコーディングを指定

                try (CloseableHttpResponse generateResponse = httpClient.execute(generatePost)) {
                    int statusCode = generateResponse.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        String errorJson = EntityUtils.toString(generateResponse.getEntity());
                        return ResponseEntity.status(statusCode).body(new AudioResponse(null, "failed to generate audio: " + errorJson));
                    }

                    String generateResponseJson = EntityUtils.toString(generateResponse.getEntity());
                    JSONObject generateData = new JSONObject(generateResponseJson);
                    String audioUrl = generateData.getString("audio_url"); // audio_url

                    // オーディオファイルをダウンロード
                    try (CloseableHttpResponse audioResponse = httpClient.execute(new HttpGet(audioUrl))) {
                        if (audioResponse.getStatusLine().getStatusCode() != 200) {
                            String errorJson = EntityUtils.toString(audioResponse.getEntity());
                            return ResponseEntity.status(audioResponse.getStatusLine().getStatusCode()).body(new AudioResponse(null, "failed to download audio: " + errorJson));
                        }

                        byte[] audioBytes = EntityUtils.toByteArray(audioResponse.getEntity());
                        String audioBase64 = java.util.Base64.getEncoder().encodeToString(audioBytes);

                        return ResponseEntity.ok(new AudioResponse(audioBase64, null));
                    }

                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AudioResponse(null, "Internal Server Error: " + e.getMessage()));
        }
    }
}