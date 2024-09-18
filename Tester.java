import java.io.File;
import java.io.IOException;

public class Tester {
    public static void main(String[] args) throws IOException {
        testInitRepo();
    }

    public static void testInitRepo() throws IOException {
        boolean success = true;
        for (int i = 0; i < 100; i++) {
            Git.initRepoHere();
            if (!checkForRequisites()) {
                success = false;
                break;
            }
            removeRepository();
        }
        if (success)
            System.out.println("Init repo method functioned successfully");
        else
            System.out.println("Init repo method failed");
    }

    private static void removeRepository () {
        File gitFolder = new File("git");
        removedirectory(gitFolder.getPath());
    }

    private static void removedirectory(String directoryName) {
        File directory = new File(directoryName);
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                removedirectory(file.getPath());
            file.delete();
        }
        directory.delete();
    }

    // Returns true if the requisite folders for the repository are present
    private static boolean checkForRequisites() {
        File gitFolder = new File("git");
        File objectFolder = new File("git/objects");
        File indexFile = new File("git/index");

        return gitFolder.exists() && objectFolder.exists() && indexFile.exists();
    }

    // Generates a random string of length 1-50
    private static String randomString() {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < (int) (Math.random() * 50 + 1); i++) {
            name.append(randomChar());
        }
        return name.toString();
    }

    // Generates a random lowercase character
    private static char randomChar() {
        return (char) (int) (Math.random() * (122 - 97) + 97);
    }
}
