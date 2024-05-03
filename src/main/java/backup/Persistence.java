package backup;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Persistence {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DIR_PATH = USER_HOME + "\\AppData\\Roaming\\D2RBackup";

    private static boolean backupCheck() {
        if (!backupDirExists()) {
            return false;
        }
        File[] files = new File(DIR_PATH).listFiles();
        if (files == null) {
            return false;
        }
        return files.length != 0;
    }

    @Nullable
    public static String[] getBackupData() {
        if (!backupCheck()) {
            return null;
        }
        File[] files = new File(DIR_PATH).listFiles();
        String[] profiles = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            profiles[i] = files[i].getName();
        }
        return profiles;
    }

    @Nullable
    public static String[] getBackedUpProfiles() {
        if (!backupCheck()) {
            return null;
        }
        File[] files = new File(DIR_PATH).listFiles();
        String[] profiles = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String backupName = files[i].getName();
            profiles[i] = backupName.substring(0, backupName.indexOf(" "));
        }
        return profiles;
    }

    @Nullable
    public static String[] getBackedUpDates() {
        if (!backupCheck()) {
            return null;
        }
        File[] files = new File(DIR_PATH).listFiles();
        String[] dates = new String[files.length];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd HH-mm-ss");
        for (int i = 0; i < files.length; i++) {
            String backupName = files[i].getName();
            String initDate = backupName.substring(backupName.indexOf(" ") + 1);
            LocalDateTime dateTime = LocalDateTime.parse(initDate, formatter);
            String time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
            String date = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
            String timeStamp = date + " " + time;
            dates[i] = timeStamp;
        }
        return dates;
    }

    public static boolean backupDirExists() {
        return new File(DIR_PATH).exists();
    }

    public static boolean createBackupDir() {
        if (backupDirExists()) {
            return false;
        }
        return new File(DIR_PATH).mkdirs();
    }

    public static String getDirPath() {
        return DIR_PATH;
    }

}
