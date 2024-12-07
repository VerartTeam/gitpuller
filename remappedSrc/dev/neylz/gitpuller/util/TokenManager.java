package dev.neylz.gitpuller.util;

import dev.neylz.gitpuller.GitPuller;

public class TokenManager {
    private static TokenManager instance;
    private String token;

    private TokenManager() {
        String tk = System.getenv("GITPULLER_TOKEN");
        if (tk != null) {
            this.token = tk;
            GitPuller.LOGGER.info("Token loaded from environment variable.");
        } else {
            GitPuller.LOGGER.warn("No token found in environment variable.");

            tk = ModConfig.CONFIG.getOrDefault("gitpuller.key", null);
            if (tk != null && !tk.isEmpty()) {
                this.token = tk;
                GitPuller.LOGGER.info("Token loaded from config.");
            } else {
                GitPuller.LOGGER.warn("No token found in config.");
            }
        }
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
