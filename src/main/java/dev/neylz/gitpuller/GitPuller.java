package dev.neylz.gitpuller;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.neylz.gitpuller.util.ModRegisteries.registerAll;

@Environment(EnvType.SERVER)
public class GitPuller implements ModInitializer {
    public static final String MOD_ID = "gitpuller";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        registerAll();

        LOGGER.info("GitPuller initialized!");
    }
}
