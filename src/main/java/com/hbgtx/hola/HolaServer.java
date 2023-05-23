package com.hbgtx.hola;

import com.hbgtx.hola.manager.ConnectionManager;
import com.hbgtx.hola.utils.Util;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import static com.hbgtx.hola.utils.ConstantUtils.DEFAULT_PORT;

public class HolaServer {
    public static void main(String[] args) {
        int port = getPortFromArguments(args);
        ConnectionManager connectionManager;
        try {
            System.out.println("Starting server : " + Inet6Address.getLocalHost().getHostAddress() +":" +port);
            connectionManager = ConnectionManager.getInstance(port);
            connectionManager.start();
        } catch (UnknownHostException e) {
            System.out.println("Cannot find ip address.");
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(connectionManager::stopListening));
    }

    private static int getPortFromArguments(String[] args) {
        for (String arg : args) {
            String[] keyValue = arg.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("port") && Util.isInteger(keyValue[1])) {
                return Integer.parseInt(keyValue[1]);
            } else {
                System.out.println("Invalid Argument, " + arg);
                System.out.println("Please add argument in format port=<port_number> to use custom port.");
            }
        }
        return DEFAULT_PORT;
    }



}