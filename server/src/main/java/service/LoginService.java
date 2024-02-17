package service;

import model.AuthData;
import models.LoginRequest;
import models.LoginResult;

public class LoginService {

    private boolean checkPassword(LoginRequest req) {

    }

    private boolean checkAuth(AuthData req) {

    }


    private String generateAuth() {

    }

    private void addToAuthData(String auth) {

    }

    private void deleteFromAuthData(AuthData req) {}

    public AuthData getLogin(LoginRequest req) {
        String auth = null;
        if(checkPassword(req)) {
            auth = generateAuth();
            addToAuthData(auth);
        }

        return new AuthData(req.username(), auth);
    }

    public void getLogout(AuthData req) {
        if(checkAuth(req)) {
            deleteFromAuthData(req);
        }
    }


}
