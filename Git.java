import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Git {

    private static boolean compressData = false;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

    }

    /**
     * Creates the requisite files and directories for a repository in this folder.
     * 
     * @throws IOException
     */
    public static void initRepoHere() throws IOException {
        // Initiate requisite files
        File gitFolder = new File("git");
        File objectFolder = new File("git/objects");
        File indexFile = new File("git/index");

        // Checks if repository has already been created
        if (repoExistsHere())
            System.out.println("Git Repository already exists");

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

    /**
     * @return True if a repository exists at this directory
     */
    private static boolean repoExistsHere() {
        File gitFolder = new File("git");
        File objectFolder = new File("git/objects");
        File indexFile = new File("git/index");
        return gitFolder.exists() && objectFolder.exists() && indexFile.exists();
    }

    /**
     * Creates a BLOB of the file in the objects folder and updates the index to
     * reflect that new hash-filename pair. File will be zip-compressed before
     * backing up if zip compression is enabled.
     * 
     * @param pathToFile - the file to backup as a blob
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void createBlob(String pathToFile) throws NoSuchAlgorithmException, IOException {
        File file = new File(pathToFile);
        if (!file.exists())
            throw new FileNotFoundException();
        if (!repoExistsHere())
            throw new FileNotFoundException("No repository found at this directory.");

        // compresses the file first for that sweet sweet S+ super credit
        if (compressData) {
            String zipPath = pathToFile + ".zip";
            zipCompressFile(pathToFile);
            String hash = generateHash(zipPath);
            File backup = new File("git/objects/" + hash);
            if (!backup.exists())
                createBackup(zipPath, hash);
            if (!indexContainsFile(pathToFile))
                updateIndex(pathToFile, hash);
            Files.delete(Path.of(zipPath));
        }

        // same as above without compression; creates the backup with the hash as its
        // filename and updates the index to show this pair
        else {
            String hash = generateHash(pathToFile);
            File backup = new File("git/objects/" + hash);
            // if the backup already exists, no need to create a new one
            if (!backup.exists())
                createBackup(pathToFile, hash);
            if (!indexContainsFile(pathToFile))
                updateIndex(pathToFile, hash);
        }
    }

    private static boolean indexContainsFile(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        String fileName = file.getName();
        BufferedReader reader = new BufferedReader(new FileReader("git/index"));
        while (reader.ready()) {
            String line = reader.readLine();
            if (Objects.equals(line.substring(line.length() - fileName.length()), fileName)) {
                reader.close();
                return true;
            }
        }
        reader.close();
        return false;
    }

    /**
     * Zip compresses a file and stores under the same name it with the .zip suffix.
     * 
     * @param pathToFile - the file to be zipped
     * @throws IOException
     */
    private static void zipCompressFile(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        String zipPath = pathToFile + ".zip";
        Files.deleteIfExists(Path.of(zipPath));
        FileInputStream fileInputStream = new FileInputStream(pathToFile);
        FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        ZipEntry zipEntry = new ZipEntry(pathToFile);
        zipEntry.setTime(0);
        zipOutputStream.putNextEntry(zipEntry);
        // could cause problems with file sizes larger than 2 billion bits/bytes
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        zipOutputStream.write(data);
        zipOutputStream.close();
    }

    /**
     * Updates the index, storing the file name and corresponding hash together with
     * proper formatting.
     * 
     * @param pathToFile - the path to the file
     * @param hash       - the hash of the file
     * @throws IOException
     */
    private static void updateIndex(String pathToFile, String hash) throws IOException {
        File index = new File("git/index");
        BufferedWriter writer = new BufferedWriter(new FileWriter(index, true));
        writer.write(hash + " " + pathToFile.substring(pathToFile.lastIndexOf("/") + 1));
        writer.newLine();
        writer.close();
    }

    /**
     * Copies the contents of the original file to the backup in git/objects/ and
     * names it based on the hash.
     * 
     * @param pathToFile - the path to the file to be backed up
     * @param hash       - the hash of the file to be backed up
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private static void createBackup(String pathToFile, String hash) throws IOException, NoSuchAlgorithmException {
        Path source = Path.of(pathToFile);
        Path destination = Path.of("git", "objects", hash);
        Files.copy(source, destination);
    }

    /**
     * Generates the hash for a file based on the data in the file.
     * 
     * @param pathToFile - the path to the file
     * @return The SHA-1 hash of the file based on its byte content
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
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

    /**
     * @param array - the byte array to be converted
     * @return the byte array as a hexidecimal number stored in String form
     */
    private static String byteArrayToHexString(byte[] array) {
        StringBuilder string = new StringBuilder();
        for (byte b : array) {
            string.append(String.format("%02X", b));
        }
        return string.toString();
    }

    /**
     * Toggles data compression before backing up when creating a blob.
     */
    public static void toggleDataCompression() {
        compressData = !compressData;
    }

    /**
     * @return True if data compression before backing up is enabled for creating
     *         blobs
     */
    public static boolean dataCompressionEnabled() {
        return compressData;
    }
}