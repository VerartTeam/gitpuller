package dev.neylz.gitpuller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.neylz.gitpuller.util.GitUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.eclipse.jgit.api.Git;

import java.io.File;

public class GitCheckoutCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("git")
            .then(CommandManager.literal("checkout")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("pack name", StringArgumentType.word()).suggests(
                    (ctx, builder) -> CommandSource.suggestMatching(GitUtil.getTrackedDatapacks(ctx.getSource().getServer().getSavePath(WorldSavePath.DATAPACKS).toFile()), builder))
                    .then(CommandManager.argument("branch", StringArgumentType.greedyString()).suggests(
                        (ctx, builder) -> CommandSource.suggestMatching(GitUtil.getBranches(new File(ctx.getSource().getServer().getSavePath(WorldSavePath.DATAPACKS).toFile(), StringArgumentType.getString(ctx, "pack name"))), builder))
                    .executes(
                        (ctx) -> checkout(ctx, StringArgumentType.getString(ctx, "pack name"), StringArgumentType.getString(ctx, "branch"))
                    ))
                )
            )
        );
    }

    private static int checkout(CommandContext<ServerCommandSource> ctx, String pack, String branch) throws CommandSyntaxException {

        File packDir = new File(ctx.getSource().getServer().getSavePath(WorldSavePath.DATAPACKS).toFile(), pack);
        if (!packDir.exists()) {
            throw new CommandSyntaxException(null, () -> "Datapack " + pack + " does not exist");
        } else if (!GitUtil.isGitRepo(packDir)) {
            throw new CommandSyntaxException(null, () -> "Datapack " + pack + " is not a git repository");
        }

        if (!gitCheckout(packDir, branch)) {
            throw new CommandSyntaxException(null, () -> "Failed to checkout branch " + branch + " in " + pack);
        } else {
            ctx.getSource().sendFeedback(() -> Text.literal("Checked out branch " + branch + " in " + pack), true);
        }

        return 1;

    }

    private static boolean gitCheckout(File file, String branch) {
        try (Git git = Git.open(file)) {
            git.checkout().setName(branch).call();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
