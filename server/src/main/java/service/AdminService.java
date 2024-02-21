package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;

public class AdminService {
    private final MemAuthDao authAccess;
    private final MemGameDao gameAccess;
    private final MemUserDao userAccess;

    public AdminService(MemAuthDao authAccess, MemGameDao gameAccess, MemUserDao userAccess) {
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public void clear() throws Exception{
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }

}
