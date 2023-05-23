package com.hbgtx.hola.callbacks;

import com.hbgtx.hola.handlers.UserHandler;
import com.hbgtx.hola.models.EntityId;

public interface UserIdCallback {
    void onUserIdReceived(EntityId userId, UserHandler userHandler);
    void onUserDisconnected(EntityId userId);
}
