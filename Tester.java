import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Tester {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        testCreateBlob();
    }

    // not yet working
    public static void testCreateBlob() throws IOException, NoSuchAlgorithmException {
        String fileName = randomString(5);
        String fileData = randomString(randomInt(0, 100));
        generateTestBlob(fileName, fileData);

    }

    
    /**
     * Creates a blob & updates the index based on the file name and data
     * @param fileName - the name of the file
     * @param fileData - the data in the file
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private static void generateTestBlob(String fileName, String fileData) throws IOException, NoSuchAlgorithmException {
        File file = new File(fileName);
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fileData);
        writer.close();
        Git.createBlob(file.getPath());
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

    private static void removeRepository() {
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

    /**
     * @param length - the length of the string
     * @return a string of random lower case letters
     */
    private static String randomString(int length) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < length; i++) {
            name.append(randomChar());
        }
        return name.toString();
    }

    /**
     * @return a random lower case character
     */
    private static char randomChar() {
        return (char) (int) (Math.random() * (122 - 97) + 97);
    }

    /**
     * @param low  - the low value
     * @param high - the high value
     * @return a random int between low and high: inclusive, exclusive
     */
    private static int randomInt(int low, int high) {
        return (int) (Math.random() * (high - low) + low);
    }
}
