package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class GitAPI {

    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        runAPI();
    }
    public static void runAPI(){
        System.out.println("Hello, input what you want to do: ");
        boolean running = true;
        while(running) {
            System.out.println("1 - Show repositories\n" +
                    "0 - Exit program");
            int choice = new Scanner(System.in).nextInt();
            switch(choice) {
                case 1:
                    System.out.println("input username to show repositories:");
                    String username = new Scanner(System.in).nextLine();
                    try {
                        listUserRepositories(username);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    running = false;
                    System.out.println("Bye! :)");
                    break;
            }
        }
    }
    public static void listUserRepositories(String username) throws IOException, ParseException {
        String url = GITHUB_API_URL + "/users/" + username + "/repos";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(URI.create(url));
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    JsonNode repos = objectMapper.readTree(jsonResponse);

                    for (JsonNode repo : repos) {
                        if (!repo.get("fork").asBoolean()) {
                            String repoName = repo.get("name").asText();
                            String ownerLogin = repo.get("owner").get("login").asText();
                            System.out.println("Repository Name: " + repoName);
                            System.out.println("Owner Login: " + ownerLogin);
                            listBranches(ownerLogin, repoName);
                            System.out.println();
                        }
                    }
                } else if (statusCode == 404) {
                    handleUserNotFound(username, statusCode);
                } else {
                    System.out.println("Error: Received status code " + statusCode);
                }
            }
        }
    }

    private static void listBranches(String owner, String repo) throws IOException, ParseException {
        String url = GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/branches";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(URI.create(url));
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    JsonNode branches = objectMapper.readTree(jsonResponse);

                    for (JsonNode branch : branches) {
                        String branchName = branch.get("name").asText();
                        String lastCommitSha = branch.get("commit").get("sha").asText();
                        System.out.println("Branch Name: " + branchName);
                        System.out.println("Last Commit SHA: " + lastCommitSha);
                    }
                } else {
                    System.out.println("Error: Received status code " + statusCode);
                }
            }
        }
    }

    private static void handleUserNotFound(String username, int statusCode) {
        String responseMessage = """
                {
                    "status": %d
                    "message": User '%s' not found.
                }
                """.formatted(statusCode, username);
        System.out.println(responseMessage);
    }
}

