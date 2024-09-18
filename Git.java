import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        updateIndex("test", generateHash("test"));
    }

    // Creates the requisite files and directories for a repository in this folder
    public static void initRepoHere() throws IOException {
        // Initiate requisite files
        File gitFolder = new File("git");
        File objectFolder = new File("git/objects");
        File indexFile = new File("git/index");

        // Checks if repository has already been created
        if (gitFolder.exists() && objectFolder.exists() && indexFile.exists()) {
            System.out.println("Git Repository already exists");
        }

        // Create requisite directories and files if they don't already exist
        else {
            if (!gitFolder.exists())
                gitFolder.mkdir();
            if (!objectFolder.exists())
                objectFolder.mkdirs();
            if (!indexFile.exists())
                indexFile.createNewFile();
        }
    }

    // Not yet working
    public static void createBlobHere(String pathToFile) throws NoSuchAlgorithmException, IOException {
        String hash = generateHash(pathToFile);

        // should compress the file first for that sweet sweet S+ super credit
        createBackup(pathToFile, hash);
        updateIndex(pathToFile, hash);
    }

    //Updates the index with the file name and hash
    private static void updateIndex(String pathToFile, String hash) throws IOException {
        File index = new File("git/index");
        BufferedWriter writer = new BufferedWriter(new FileWriter(index, true));
        writer.write(hash + " " + pathToFile.substring(pathToFile.lastIndexOf("/") + 1));
        writer.newLine();
        writer.close();
    }

    // copies the contents of the original file to the backup
    private static void createBackup(String pathToFile, String hash) throws IOException, NoSuchAlgorithmException {
        Path source = Path.of(pathToFile);
        Path destination = Path.of("git", "objects", hash);
        Files.copy(source, destination);
    }

    // Generates the hash for a file based on the data in the file
    private static String generateHash(String pathToFile) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        File file = new File(pathToFile);
        // this could cause issues if the file is greater than 2 billion bytes/bits idk
        // which
        byte[] byteData = new byte[(int) file.length()];

        // Reads the byte data into a byte array
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(byteData);
        inputStream.close();

        // Hashes the byte data via the SHA-1 algorithm
        byte[] hash = md.digest(byteData);

        return byteArrayToHexString(hash);
    }

    // turns a byte array into a hexidecimal number stored in String form
    private static String byteArrayToHexString(byte[] array) {
        StringBuilder string = new StringBuilder();
        for (byte b : array) {
            string.append(String.format("%02X", b));
        }
        return string.toString();
    }

    // Prints an array of bytes to the console
    private static void printByteArray(byte[] array) {
        for (byte b : array) {
            System.out.print(b + ", ");
        }
        System.out.println();
    }
}