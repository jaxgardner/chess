package ui;

import com.google.gson.Gson;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.List;
import java.util.Map;

import Exception.ClientException;
import com.google.gson.reflect.TypeToken;
import model.*;

public class ServerFacade {
    private final String serverUrl;
    private String authToken = null;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String registerUser(UserData user) throws ClientException {
        String path = "/user";
        AuthData auth = makeRequest("POST", path, user, AuthData.class);
        if(auth != null) {
            authToken = auth.authToken();
        }
        return "Registered!";
    }

    public String loginUser(LoginRequest req) throws ClientException {
        String path = "/session";
        AuthData auth = makeRequest("POST", path, req, AuthData.class);
        if(auth != null) {
            authToken = auth.authToken();
            return "Logged in!";
        }
        return "Error";
    }

    public String logoutUser() throws ClientException {
        String path = "/session";
        if(authToken != null) {
            makeRequest("DELETE", path, null, null);
            authToken = null;
            return "Logged out!";
        }
        return "Unauthorized";
    }

    public String createGame(CreateGameRequest req) throws ClientException {
        String path = "/game";

        CreateGameResponse res = makeRequest("POST", path, req, CreateGameResponse.class);

        return "Game created!";
    }

    public List<GameListResult> listGames() throws ClientException {
        String path = "/game";

        Map<String, List<GameListResult>> res = makeRequest("GET", path, null, Map.class);
        return res.get("games");
    }

    public String joinGame(JoinGameRequest req) throws ClientException {
        String path = "/game";
        GameListResult game;
        List<GameListResult> results = listGames();
        try {
            game = results.get(req.gameID() - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new ClientException("Invalid game");
        }
        int gameId = game.gameID();
        JoinGameRequest newReq = new JoinGameRequest(req.playerColor(), gameId);

        makeRequest("PUT", path, newReq, null);
        return "Joined game!";
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ClientException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if(authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ClientException {
        var status = http.getResponseCode();
        String message = "failure: ";
        if (!isSuccessful(status)) {
            if(status == 401) {
                message = "Invalid username or password: ";
            }
            else if(status == 400) {
                message = "Invalid request: ";
            }
            else if(status == 403) {
                message = "Username already taken: ";
            }
            throw new ClientException(message + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    if(responseClass == Map.class) {
                        Type listType = new TypeToken<Map<String, List<GameListResult>>>() {}.getType();
                        response = new Gson().fromJson(reader, listType);
                    } else {
                        response = new Gson().fromJson(reader, responseClass);
                    }
                }
            }
        }
        return response;
    }

    public String getAuthToken() {
        return authToken;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
