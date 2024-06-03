package dev.neylz.gitpuller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.neylz.gitpuller.util.TokenManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GitTokenCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("git")
                .then(CommandManager.literal("token")
                    .requires((source) -> source.hasPermissionLevel(4))
                    .then(CommandManager.argument("token", StringArgumentType.greedyString())
                        .executes((context) -> setToken(context, StringArgumentType.getString(context, "token"))
                    )
                )
            )
        );
    }

    private static int setToken(CommandContext<ServerCommandSource> ctx, String tk) throws CommandSyntaxException {
        TokenManager.getInstance().setToken(tk);

        ctx.getSource().sendFeedback(() -> {
            return Text.literal("Git organization token has been set.").formatted(Formatting.GREEN);
        }, true);

        return 1;
    }
}
