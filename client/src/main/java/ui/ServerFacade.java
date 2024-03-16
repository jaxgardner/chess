package ui;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import Exception.ClientException;
import model.AuthData;
import model.LoginRequest;
import model.UserData;

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
        }
        return "Logged in!";
    }

    public String logoutUser() throws ClientException {
        String path = "/session";
        makeRequest("DELETE", path, null, null);
        return "Logged out!";
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
        if (!isSuccessful(status)) {
            throw new ClientException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
