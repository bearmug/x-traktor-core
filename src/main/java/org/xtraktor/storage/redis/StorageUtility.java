package org.xtraktor.storage.redis;

import java.io.IOException;
import java.net.ServerSocket;

public class StorageUtility {

    private final int suggestedPort;

    public StorageUtility(int suggestedPort) {
        this.suggestedPort = suggestedPort;
    }

    public int getFreePort() {
        try (ServerSocket tempServer = new ServerSocket(suggestedPort)) {
            return tempServer.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Can not allocate free port", e);
        }
    }
}
