package com.hbgtx.hola.database;

import com.hbgtx.hola.handlers.UserHandler;
import com.hbgtx.hola.models.EntityId;

import java.util.List;

public class DBHelper {
    private final UserConnectionDatabase userConnectionDatabase;

    public DBHelper() {
        userConnectionDatabase = UserConnectionDatabase.getInstance();
    }

    public void addUserHandler(String userId, UserHandler userHandler) {
        userConnectionDatabase.addUserHandler(userId, userHandler);
    }

    public void deleteUserHandler(EntityId userId) {
        userConnectionDatabase.deleteUserHandler(userId);
    }

    public UserHandler getUserHandler(EntityId userId) {
        return userConnectionDatabase.getUserHandler(userId);
    }

    public List<UserHandler> getAndDeleteAllUserHandlers() {
        return userConnectionDatabase.getAndDeleteAllHandlers();
    }
}
