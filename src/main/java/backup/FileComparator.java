package backup;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileComparator {

    private static boolean equalNames(@NotNull File file1, @NotNull File file2) {
        return file1.getName().equals(file2.getName());
    }

    /**
     * Parameters must be sorted.
     * @param files1
     * @param files2
     * @return
     */
    private static boolean equalNames(@NotNull File[] files1, @NotNull File[] files2) {
        if (files1.length != files2.length) {
            return false;
        }
        for (int i = 0; i < files1.length; i++) {
            if (!equalNames(files1[i], files2[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean equalContents(@NotNull File file1, @NotNull File file2) {
        if (file1.length() != file2.length() || file1.isDirectory() || file2.isDirectory()) {
            return false;
        }
        long fileSize = file1.length();
        try (
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2)
        ) {
            for (long i = 0; i < fileSize; i++) {
                if (fis1.read() != fis2.read()) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Parameters must be sorted.
     * @param files1
     * @param files2
     * @return
     */
    private static boolean equalContents(@NotNull File[] files1, @NotNull File[] files2) {
        if (files1.length != files2.length) {
            return false;
        }
        for (int i = 0; i < files1.length; i++) {
            if (!equalContents(files1[i], files2[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasDirectory(@NotNull File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(@NotNull File file1, @NotNull File file2) {
        if (file1.isDirectory() || file2.isDirectory()) {
            return false;
        }
        return equalNames(file1, file2) && equalContents(file1, file2);
    }

    /**
     * Parameters must be sorted.
     * @param files1
     * @param files2
     * @return
     */
    public static boolean equals(@NotNull File[] files1, @NotNull File[] files2) {
        return files1.length == files2.length &&
                equalNames(files1, files2) &&
                equalContents(files1, files2) &&
                !hasDirectory(files1) &&
                !hasDirectory(files2);
    }

}
