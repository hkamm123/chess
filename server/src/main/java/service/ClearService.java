package service;

import dataaccess.AuthDao;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import dataaccess.UserDao;
import server.ClearResult;

public class ClearService {
    public ClearResult clear() {
        return new ClearResult(null);
    }
}
