package dev.neylz.gitpuller.util;

import com.mojang.datafixers.util.Pair;
import dev.neylz.gitpuller.GitPuller;

public class ModConfig {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    private static String GITPULLER_TOKEN;


    public static void register() {

        configs = new ModConfigProvider();
        createConfig();

        CONFIG = SimpleConfig.of(GitPuller.MOD_ID + "config").provider(configs).request();

        assignConfigs();

    }

    private static void createConfig() {
        configs.addKeyValuePair(new Pair<>("gitpuller.key", ""), "Provide your key here. You can also provide it via environment variable GITPULLER_TOKEN or in game with /gitpuller token <key>");
    }

    private static void assignConfigs() {
        GITPULLER_TOKEN = CONFIG.getOrDefault("gitpuller.key", null);

        GitPuller.LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
    }
}
