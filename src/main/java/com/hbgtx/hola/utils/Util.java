package com.hbgtx.hola.utils;

import com.hbgtx.hola.models.EntityId;

import static com.hbgtx.hola.utils.ConstantUtils.RESERVED_SERVER_ID;

public class Util {
    public static boolean isInteger(String value) {
        if (value == null) {
            return false;
        }
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isServerId(EntityId id) {
        return id.getId().equals(RESERVED_SERVER_ID);
    }
}
