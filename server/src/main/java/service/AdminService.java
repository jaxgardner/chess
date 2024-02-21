package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;

public class AdminService {
    private final MemAuthDao authAccess;
    private final MemGameDao gameAccess;
    private final MemUserDao userAccess;

    public AdminService() {
        authAccess = new MemAuthDao();
        gameAccess = new MemGameDao();
        userAccess = new MemUserDao();
    }

    public void clear() throws Exception{
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }

}
