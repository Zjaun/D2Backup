package backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ProfileFetcher {

    private static final String[] FILE_EXTENSIONS = {".ctl", ".d2s", ".key", ".ma0", ".ma2", ".map"};
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String D2RSaveFilesPath = USER_HOME + "\\Saved Games\\Diablo II Resurrected";
    private static final Filter FILTER = new Filter();

    static class Filter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".ctl");
        }

    }

    private static File getSavedFilesDir(String D2RSaveFilesPath) throws FileNotFoundException {
        File file = new File(D2RSaveFilesPath);
        if (!file.exists()) {
            throw new FileNotFoundException(
                    "Directory of Saved Files for Diablo II Resurrected does not exist."
            );
        }
        if (!file.isDirectory()) {
            // there is a chance that there's a file named "Diablo II Resurrected" without any extensions
            throw new IllegalStateException(
                    "There's a file named Diablo II Resurrected, but it is not a directory or folder."
            );
        }
        return file;
    }

    public static String[] getProfiles() throws FileNotFoundException {
        File savedFilesDir = getSavedFilesDir(D2RSaveFilesPath);
        String[] fileNames = savedFilesDir.list(FILTER);
        if (fileNames == null) {
            return new String[0];
        }
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = fileNames[i].replace(".ctl", "");
        }
        return fileNames;
    }

    public static File[] getSavedFiles(String profile) throws FileNotFoundException {
        if (!exists(profile)) {
            throw new FileNotFoundException("Profile not found.");
        }
        List<File> files = new ArrayList<>();
        for (String extension : FILE_EXTENSIONS) {
            File savedFile = new File(D2RSaveFilesPath + "\\" + profile + extension);
            if (!savedFile.exists()) {
                continue;
            }
            files.add(savedFile);
        }
        int fileCount = files.size();
        File[] fileArray = new File[fileCount];
        for (int i = 0; i < fileCount; i++) {
            fileArray[i] = files.get(i);
        }
        return fileArray;
    }

    public static long getLastModified(String profile) throws FileNotFoundException {
        if (!exists(profile)) {
            throw new FileNotFoundException("Profile not found.");
        }
        String path = D2RSaveFilesPath + "\\" + profile + ".ctl";
        File file = new File(path);
        return file.lastModified();
    }

    public static File[] getAllSavedProfiles() throws FileNotFoundException {
        return getSavedFilesDir(D2RSaveFilesPath).listFiles(FILTER);
    }

    public static boolean exists(String profileName) throws FileNotFoundException {
        String[] profiles = getProfiles();
        for (String profile : profiles) {
            if (profile.equals(profileName)) {
                return true;
            }
        }
        return false;
    }

    public static String getD2RSaveFilesPath() {
        return D2RSaveFilesPath;
    }

    public static boolean folderExists() {
        return new File(D2RSaveFilesPath).exists();
    }

}
