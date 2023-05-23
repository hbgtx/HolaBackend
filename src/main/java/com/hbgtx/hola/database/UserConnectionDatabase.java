package com.hbgtx.hola.database;

import com.hbgtx.hola.handlers.UserHandler;
import com.hbgtx.hola.models.EntityId;

import java.util.*;

// TODO: Replace with actual database
public class UserConnectionDatabase {
    private static final Object mutex = new Object();
    private static UserConnectionDatabase userConnectionDatabase;
    private final HashMap<String, UserHandler> userHandlerMap;

    private UserConnectionDatabase() {
        this.userHandlerMap = new HashMap<>();
    }

    public static UserConnectionDatabase getInstance() {
        return Objects.requireNonNullElseGet(userConnectionDatabase, () -> userConnectionDatabase =
                new UserConnectionDatabase());
    }

    public void addUserHandler(String userId, UserHandler userHandler) {
        synchronized (mutex) {
            userHandlerMap.remove(userId);
            userHandlerMap.put(userId, userHandler);
        }
    }

    public UserHandler getUserHandler(EntityId userId) {
        synchronized (mutex) {
            return userHandlerMap.get(userId.getId());
        }
    }

    public void deleteUserHandler(EntityId userId) {
        synchronized (mutex) {
            if (userId != null) {
                userHandlerMap.remove(userId.getId());
            }
        }
    }

    public List<UserHandler> getAndDeleteAllHandlers() {
        synchronized (mutex) {
            List<UserHandler> userHandlers = List.copyOf(userHandlerMap.values());
            userHandlerMap.clear();
            return userHandlers;
        }
    }


}
