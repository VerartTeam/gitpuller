package dev.neylz.gitpuller.util;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class GitUtil {

    @NotNull
    public static String getCurrentBranch(File file) {
        if (!file.exists()) return "";

        try (Git git = Git.open(file)) {
            Ref head = git.getRepository().findRef("HEAD");
            if (head.isSymbolic()) {
                return head.getTarget().getName().substring("refs/heads/".length());
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @NotNull
    public static String getCurrentHeadSha1(File file, int length) {
        if (!file.exists()) return "";

        try (Git git = Git.open(file)) {
            ObjectId head = git.getRepository().resolve("HEAD");
            if (head != null) {
                return head.name().substring(0, Math.min(length, head.name().length()));
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static boolean isGitRepo(File file) {
        if (!file.exists()) return false;

        try (Git git = Git.open(file)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
