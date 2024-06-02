package dev.neylz.gitpuller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GitCheckoutCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("git")
                .then(CommandManager.literal("checkout")
                        .requires((source) -> source.hasPermissionLevel(2))
                        .executes(GitCheckoutCommand::run)
                )
        );
    }

    private static int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return 1;
    }
}
