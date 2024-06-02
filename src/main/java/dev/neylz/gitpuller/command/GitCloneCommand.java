package dev.neylz.gitpuller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

public class GitCloneCommand {
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].\\S*$");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
            CommandManager.literal("git").then((
                CommandManager.literal("clone").requires((source) -> source.hasPermissionLevel(2))).then((
                    CommandManager.literal("datapack")).then((
                        CommandManager.argument("name", StringArgumentType.string())).then((
                            CommandManager.argument("url", StringArgumentType.greedyString()).executes((context) -> {
                                return cloneDatapack(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "url"));
                            })
                        ))
                    )
                )
            )

        );
    }

    private static int cloneDatapack(CommandContext<ServerCommandSource> ctx, String name, String remoteUrl) throws CommandSyntaxException {
        if (!URL_PATTERN.matcher(remoteUrl).matches()) {
            throw new CommandSyntaxException(null, new Message() {
                @Override
                public String getString() {
                    return "Invalid URL: " + remoteUrl;
                }
            });
        }


        ctx.getSource().sendFeedback(() -> {
            return Text.empty()
                    .append(Text.literal("Cloning from ").formatted(Formatting.RESET))
                    .append(Text.literal(remoteUrl).formatted(Formatting.AQUA))
                    .append(Text.literal(" into the datapack ").formatted(Formatting.RESET))
                    .append(Text.literal("[" + name + "]").formatted(Formatting.YELLOW));
        }, false);



        return 1;
    }

}
