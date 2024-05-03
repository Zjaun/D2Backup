package ui;

import backup.Backup;
import backup.Persistence;
import backup.ProfileFetcher;
import backup.Restore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Console {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private record Range(int low, int high) {

        public boolean contains(int number) {
            return (number >= low && number <= high);
        }

    }

    private static String input(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static void pause() {
        System.out.println("WOOPS! Something went wrong. Contact Developer, press enter key to exit.");
        try {
            System.in.read();
        } catch (Exception ignored) {

        }
    }

    private static void clear() {
        try {
            if (OS.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception e) {
            e.printStackTrace();
            pause();
        }
    }


    public static void showOptions() {
        clear();
        System.out.println("Diablo II Remastered Backup Utility\n");
        System.out.println("[1] Backup");
        System.out.println("[2] Restore");
        System.out.println("[3] Quit\n");
        System.out.print("Choice: ");
        String response = input("");
        while (!response.equals("1") && !response.equals("2") && !response.equals("3")) {
            System.out.println("Invalid Choice. Try again.");
            System.out.print("Choice: ");
            response = input("");
        }
        System.out.println();
        if (response.equals("1")) {
            backup();
        } else if (response.equals("2")) {
            restore();
        } else {
            System.out.println("Goodbye!");
        }
    }

    private static void askOptions(String statement) {
        System.out.printf("%sWould you like to select another option? [Y/N]: ", statement);
        String response = input("");
        while (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n")) {
            System.out.println("Invalid Choice. Try again.");
            System.out.print("No profiles found. Would you like to select another option? [Y/N]: ");
            response = input("");
        }
        if (response.equalsIgnoreCase("y")) {
            showOptions();
        } else {
            System.out.println("Goodbye!");
        }
    }

    private static int profileSelector(Range range) {
        System.out.print("\nProfile: ");
        int profileNumber;
        while (true) {
            try {
                profileNumber = Integer.parseInt(input(""));
                if (range.contains(profileNumber)) {
                    break;
                } else {
                    System.out.println("Invalid Choice. Try again.");
                    System.out.print("Profile: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Choice. Try again.");
                System.out.print("Profile: ");
            }
        }
        return profileNumber;
    }

    private static void backup() {
        clear();
        if (!ProfileFetcher.folderExists()) {
            askOptions("Saved Files folder does not exist! ");
            return;
        }
        try {
            String[] profiles = ProfileFetcher.getProfiles();
            if (profiles.length == 0) {
                askOptions("No profiles found. ");
            } else {
                Range range = new Range(1, profiles.length);
                System.out.println("Please choose a profile to backup: \n");
                for (int i = 0; i < profiles.length; i++) {
                    System.out.printf("[%d] %s\n", i + 1, profiles[i]);
                }
                int profileNumber = profileSelector(range);
                try {
                    boolean backupResult = Backup.backup(profiles[profileNumber - 1]);
                    if (backupResult) {
                        askOptions("Backup successful. ");
                    } else {
                        askOptions("Backup failed. This probably because the profile does not exist.\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    pause();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            pause();
        }
    }

    private static int digitsOf(int number) {
        int digits = 1;
        while (number / 10 != 0) {
            digits++;
            number = number / 10;
        }
        return digits;
    }

    private static int getLongestStringLength(String[] strings) {
        int longest = 0;
        for (String string : strings) {
            if (string.length() > longest) {
                longest = string.length();
            }
        }
        return longest;
    }

    private static void printHeader(int spacesForProfile, int spacesForDate) {
        String separator = "-".repeat(spacesForProfile + spacesForDate + 2);
        System.out.println(separator);
        int leftPaddingLength = (int) ((spacesForProfile - 8) / 2.0);
        int rightPaddingLength = (int) Math.ceil((spacesForProfile - 8) / 2.0);
        String leftPadding = " ".repeat(leftPaddingLength);
        String rightPadding = " ".repeat(rightPaddingLength);
        String profileHeader = leftPadding + "PROFILES" + rightPadding;
        leftPaddingLength = (int) ((spacesForDate - 4) / 2.0);
        rightPaddingLength = (int) Math.ceil((spacesForDate - 4) / 2.0);
        leftPadding = " ".repeat(leftPaddingLength);
        rightPadding = " ".repeat(rightPaddingLength);
        String dateHeader = leftPadding + "DATE" + rightPadding + "|";
        System.out.println(profileHeader + "|" + dateHeader);
        System.out.println(separator);
    }

    private static void printProfiles(
            int spacesForProfile,
            int spacesForDate,
            String[] profiles,
            String[] dates
    ) {
        String separator = "-".repeat(spacesForProfile + spacesForDate + 2);
        for (int i = 0; i < profiles.length; i++) {
            String profile = profiles[i];
            String date = dates[i];
            int profilePadding = spacesForProfile - (3 + digitsOf(i) + profile.length());
            int datePadding = spacesForDate - date.length() - 1;
            String profileSpacer = " ".repeat(profilePadding);
            String dateSpacer = " ".repeat(datePadding);
            System.out.printf("[%d] %s%s| %s%s|\n", i + 1, profile, profileSpacer, date, dateSpacer);
        }
        System.out.println(separator);
    }

    private static void restore() {
        clear();
        if (!Persistence.backupDirExists()) {
            Persistence.createBackupDir();
        }
        String[] restorePoints = Persistence.getBackupData();
        if (restorePoints == null) {
            askOptions("No backup points found. ");
            return;
        }
        String[] profiles = Persistence.getBackedUpProfiles();
        String[] dates = Persistence.getBackedUpDates();
        int digitsOfProfiles = digitsOf(profiles.length);
        int longestProfileLength = getLongestStringLength(profiles);
        int longestDateLength = getLongestStringLength(dates);
        int spacesForProfile = (4 + digitsOfProfiles + longestProfileLength);
        int spacesForDate = (2 + longestDateLength);
        Range range = new Range(1, profiles.length);
        System.out.println("Please choose a profile to restore: \n");
        printHeader(spacesForProfile, spacesForDate);
        printProfiles(spacesForProfile, spacesForDate, profiles, dates);
        int profileNumber = profileSelector(range);
        try {
            boolean result = Restore.restore(restorePoints[profileNumber - 1]);
            if (result) {
                askOptions("Restore successful. ");
            }
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            pause();
        }
    }

}
