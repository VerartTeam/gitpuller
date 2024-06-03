package dev.neylz.gitpuller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.neylz.gitpuller.util.GitUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.text.Normalizer;

public class GitInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("git")
            .then(CommandManager.literal("info")
                .executes(GitInfoCommand::datapackInfo)
            )
        );
    }

    private static int datapackInfo(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        MinecraftServer server = ctx.getSource().getServer();

        File file = server.getSavePath(WorldSavePath.DATAPACKS).toFile();

        // list all files
        File[] files = file.listFiles();
        if (files != null) {
            ctx.getSource().sendFeedback(() -> {
                MutableText text = Text.empty()
                        .append(Text.literal("Available datapacks:").formatted(Formatting.UNDERLINE));
                for (File f : files) {
                    if (!f.isDirectory()) continue;

                    text.append(Text.literal("\n   ").formatted(Formatting.RESET))
                        .append(Text.literal("[" + f.getName() + "]").formatted(Formatting.YELLOW));

                    if (GitUtil.isGitRepo(f)) {
                        text.append(Text.literal("  (").formatted(Formatting.RESET))
                            .append(Text.literal(GitUtil.getCurrentBranch(f)).formatted(Formatting.DARK_GREEN))
                            .append(Text.literal("-").formatted(Formatting.RESET))
                            .append(Text.literal(GitUtil.getCurrentHeadSha1(f, 7)).formatted(Formatting.AQUA))
                            .append(Text.literal(")").formatted(Formatting.RESET));
                    } else {
                        text.append(Text.literal("  (untracked)").formatted(Formatting.RED).formatted(Formatting.ITALIC));
                    }
                }

                return text;
            }, false);
        }


        return 1;
    }

}
