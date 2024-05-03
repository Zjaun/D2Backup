package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Backup {

    private static boolean createBackup(String profile) throws IllegalStateException, IOException {
        if (!Persistence.backupDirExists()) {
            Persistence.createBackupDir();
        }
        Instant savedFileLastModified = Instant.ofEpochMilli(ProfileFetcher.getLastModified(profile));
        LocalDateTime localTime = LocalDateTime.ofInstant(savedFileLastModified, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String date = localTime.format(formatter).replaceAll("/", "_").replaceAll(":", "-");
        String path = Persistence.getDirPath() + "\\" + profile + " " + date;
        File file = new File(path);
        if (!file.exists()) {
            boolean folderCreated = file.mkdirs();
            if (!folderCreated) {
                throw new IllegalStateException("Cannot create backup folder.");
            }
        }
        File[] profileSavedFiles = ProfileFetcher.getSavedFiles(profile);
        for (File profileSavedFile : profileSavedFiles) {
            File destination = new File(path + "\\" + profileSavedFile.getName());
            Files.copy(profileSavedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return true;
    }

    /**
     *
     * @param profile
     * @return {@code true}, {@code false}
     * @throws IOException
     */
    public static boolean backup(String profile) throws IOException {
        if (!ProfileFetcher.exists(profile)) {
            return false;
        }
        return createBackup(profile);
    }

}
