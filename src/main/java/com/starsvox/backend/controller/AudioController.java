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

    private static final String API_BASE_URL = "https://apibox.erweima.ai/api/v1/generate";
    private static final String API_KEY = ""; // APIキー（AWSのシークレットキーマネージャーから取ってくるとか対応）それ以外は各自で入力して確認してもらう

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AudioResponse> generateAudio(@RequestBody AudioRequest audioRequest) {
        try {
            String prompt = audioRequest.getPrompt();

            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest().body(new AudioResponse(400, "prompt is required", null));
            }

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost generatePost = new HttpPost(API_BASE_URL);
                generatePost.addHeader("Authorization", "Bearer " + API_KEY);
                generatePost.addHeader("Content-Type", "application/json");

                JSONObject generateJson = new JSONObject();
                generateJson.put("prompt", prompt);
                generateJson.put("customMode", false);
                generateJson.put("instrumental", false);
                generateJson.put("model", "V3_5");
                generateJson.put("callBackUrl", "https://api.example.com/callback");

                generatePost.setEntity(new StringEntity(generateJson.toString(), "UTF-8"));

                try (CloseableHttpResponse generateResponse = httpClient.execute(generatePost)) {
                    int statusCode = generateResponse.getStatusLine().getStatusCode();
                    String responseJson = EntityUtils.toString(generateResponse.getEntity());

                    if (statusCode != 200) {
                        return ResponseEntity.status(statusCode).body(new AudioResponse(statusCode, "Failed to generate audio", responseJson));
                    }

                    JSONObject responseData = new JSONObject(responseJson);
                    String taskId = responseData.getJSONObject("data").getString("taskId");

                    return ResponseEntity.ok(new AudioResponse(200, "success", taskId));
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AudioResponse(500, "Internal Server Error: " + e.getMessage(), null));
        }
    }

    @GetMapping(value = "/task-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTaskInfo(@RequestParam String taskId) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = API_BASE_URL + "/record-info?taskId=" + taskId;
            HttpGet getTaskRequest = new HttpGet(url);
            getTaskRequest.addHeader("Authorization", "Bearer " + API_KEY);
            getTaskRequest.addHeader("Accept", "application/json");

            try (CloseableHttpResponse taskResponse = httpClient.execute(getTaskRequest)) {
                int statusCode = taskResponse.getStatusLine().getStatusCode();
                String responseJson = EntityUtils.toString(taskResponse.getEntity());

                if (statusCode != 200) {
                    return ResponseEntity.status(statusCode).body("Failed to retrieve task info: " + responseJson + ", Status Code: " + statusCode);
                }

                return ResponseEntity.ok(responseJson);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }
}