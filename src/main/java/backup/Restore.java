package backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Restore {

    private static final String BACKUP_DIR_PATH = Persistence.getDirPath();
    private static final String SAVED_FILES_PATH = ProfileFetcher.getD2RSaveFilesPath();

    public static boolean restore(String folderName) throws IOException, IllegalStateException {
        File file = new File(BACKUP_DIR_PATH + "\\" + folderName);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("Folder not found: %s", file.getPath()));
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalStateException("No files found in folder: " + file.getPath());
        }
        if (!ProfileFetcher.folderExists()) {
            throw new IllegalStateException("Saved Files folder does not exist: " + SAVED_FILES_PATH);
        }
        for (File profileBackedUpFile : files) {
            File destination = new File(SAVED_FILES_PATH + "\\" + profileBackedUpFile.getName());
            Files.copy(profileBackedUpFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return true;
    }

}
