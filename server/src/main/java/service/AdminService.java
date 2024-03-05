package service;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import dataAccess.UserDAO;
import exception.ServiceLogicException;

public class AdminService {
    private final UserDAO userDAO;
    private final MemAuthDao authDAO;
    private final MemGameDao gameDAO;


    public AdminService(UserDAO userDAO, MemAuthDao authDAO, MemGameDao gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public void clear() throws ServiceLogicException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access Data");
        }
    }

}
