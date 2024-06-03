package dev.neylz.gitpuller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.neylz.gitpuller.GitPuller;
import dev.neylz.gitpuller.util.GitUtil;
import dev.neylz.gitpuller.util.TokenManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class GitPullCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("git")
                .then(CommandManager.literal("pull").requires((source) -> source.hasPermissionLevel(2)).then(CommandManager.argument("pack name", StringArgumentType.word()).suggests((ctx, builder) -> {
                    return CommandSource.suggestMatching(GitUtil.getTrackedDatapacks(ctx.getSource().getServer().getSavePath(WorldSavePath.DATAPACKS).toFile()), builder);
                }).executes((ctx) -> {
                    return pullPack(ctx, StringArgumentType.getString(ctx, "pack name"));
                })))
        );
    }

    private static int pullPack(CommandContext<ServerCommandSource> ctx, String packName) throws CommandSyntaxException {
        // perform git pull -f --all on the specified pack

        File file = new File(ctx.getSource().getServer().getSavePath(WorldSavePath.DATAPACKS).toFile(), packName);

        if (!file.exists()) {
            throw new CommandSyntaxException(null, new Message() {
                @Override
                public String getString() {
                    return "Datapack " + packName + " does not exist";
                }
            });
        } else if (!GitUtil.isGitRepo(file)) {
            throw new CommandSyntaxException(null, new Message() {
                @Override
                public String getString() {
                    return "Datapack " + packName + " is not a git repository";
                }
            });
        }

        // git pull -f --all
        String sha1 = GitUtil.getCurrentHeadSha1(file, 7);
        if (!gitPull(file)) {
            throw new CommandSyntaxException(null, new Message() {
                @Override
                public String getString() {
                    return "Failed to pull changes from " + packName;
                }
            });
        }

        String newSha1 = GitUtil.getCurrentHeadSha1(file, 7);
        if (sha1.equals(newSha1)) {
            ctx.getSource().sendFeedback(() -> {
                return Text.of("No new changes pulled from " + packName);
            }, true);
        } else {
            ctx.getSource().sendFeedback(() -> {
                return Text.of("Pulled changes from " + packName + " (" + sha1 + " -> " + newSha1 + ")");
            }, true);
        }



        return 1;
    }



    private static boolean gitPull(File repoDir) {
        try {
            Git git = Git.open(repoDir);

            String branchName = git.getRepository().getBranch();
            boolean isBranch = GitUtil.getBranches(repoDir).contains(branchName);

            git.fetch()
                    .setRemoveDeletedRefs(true)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(TokenManager.getInstance().getToken(), ""))
                    .call();

            git.pull()
                    .setRebase(true)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(TokenManager.getInstance().getToken(), ""))
                    .call();

            GitPuller.LOGGER.info("Fetched changes from remote repository");
            GitPuller.LOGGER.info(git.getRepository().getBranch());

            if (!isBranch) {
                GitPuller.LOGGER.info("Fetched all changes, didn't checkout branch");
                return true;
            } else {
                git.checkout()
                        .setForceRefUpdate(true)
                        .setName(branchName)
                        .call();

                GitPuller.LOGGER.info("Checked out branch " + git.getRepository().getBranch());
            }

//            git.reset()
//                    .setMode(ResetCommand.ResetType.HARD)
//                    .setRef("refs/heads/" + git.getRepository().getBranch())
//                    .call();

            GitPuller.LOGGER.info("Reset local repository to remote HEAD");

            return true;
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }
}
