package com.hbgtx.hola.callbacks;

import com.hbgtx.hola.handlers.UserHandler;
import com.hbgtx.hola.models.EntityId;

public interface UserIdCallback {
    boolean onUserIdReceived(EntityId userId, UserHandler userHandler);
    void onUserDisconnected(EntityId userId);
}
