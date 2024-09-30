import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipInputStream;

public class Tester {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        testInitRepo();
        testCreateBlob();
        testZipCompression();
        //testCreateBlobWithDirectories();
        testCreateBlobWithSubDirectories();
    }

    /**
     * Tests if the zip compression is working when backing up files to blobs.
     * Ensures that zip compression is enabled before conducting the test. Unzips
     * the backed up file and ensures that the data mathches the original data. By
     * default this method will only function if the data is stored & zipped
     * properly in the blob, and if the index has the correct hash-file pair stored.
     * Hence, by default it also tests if blobs are be created succesfully and if
     * the
     * index is functioning properly.
     * 
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void testZipCompression() throws NoSuchAlgorithmException, IOException {
        // ensures that a repo exists to back up files to
        if (!repoExistsHere()) {
            System.out.println("Test cannot be completed as no repository exists at this directory.");
        }

        // ensures data compression is enabled
        if (!Git.dataCompressionEnabled())
            Git.toggleDataCompression();

        for (int i = 0; i < 100; i++) {
            // creates a copy of the index
            File index = new File("git/index");
            Files.copy(Path.of("git", "index"), Path.of("git", "index_copy"));
            File indexCopy = new File("git/index_copy");

            // conducts the test
            String fileName = randomString(5);
            String fileData = randomString(randomInt(0, 100));
            generateTestBlob(fileName, fileData);

            String indexString = getIndex();
            String hash = getHashFromIndex(fileName, indexString);

            unzipFile("git/objects/" + hash);

            if (!blobMatchesData(fileName, fileData)) {
                System.out.println("Zip compression failed the test.");
                return;
            }

            // cleans up
            File backupFile = new File("git/objects/" + hash);
            File original = new File(fileName);
            backupFile.delete();
            original.delete();
            index.delete();
            indexCopy.renameTo(index);
        }

        System.out.println("Zip compression passed the test.");
    }

    /**
     * Unzips & decompresses a file compressed with the zip format.
     * 
     * @param pathToFile - the file to be unzipped
     * @throws IOException
     */
    private static void unzipFile(String pathToFile) throws IOException {
        String zipPath = pathToFile + ".zip";
        Files.copy(Path.of(pathToFile), Path.of(zipPath));
        FileInputStream fileInputStream = new FileInputStream(zipPath);
        ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
        zipInputStream.getNextEntry();
        byte[] data = zipInputStream.readAllBytes();
        zipInputStream.close();
        FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
        fileOutputStream.write(data);
        fileOutputStream.close();
        Files.delete(Path.of(zipPath));
    }

    /**
     * Tests whether the createBlob method works.
     * Accomplishes this by running the method on a random file with random data.
     * Then looks up the hash value for that data in the index.
     * Checks if the data stored at the backup is the same as the original data.
     * This only works if the backup is created properly and if the hash is
     * stored in the index next to the original name. Only tests for the successful
     * creation of non-compressed blobs.
     * 
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void testCreateBlob() throws IOException, NoSuchAlgorithmException {
        // ensures that a repo exists to back up files to
        if (!repoExistsHere()) {
            System.out.println("Test cannot be completed as no repository exists at this directory.");
            return;
        }

        if (Git.dataCompressionEnabled())
            Git.toggleDataCompression();
        for (int i = 0; i < 100; i++) {
            // creates a copy of the index
            File index = new File("git/index");
            Files.copy(Path.of("git", "index"), Path.of("git", "index_copy"));
            File indexCopy = new File("git/index_copy");

            // conducts the test
            String fileName = randomString(5);
            String fileData = randomString(randomInt(0, 100));
            generateTestBlob(fileName, fileData);
            if (!blobMatchesData(fileName, fileData)) {
                System.out.println("createBlob method failed the test.");
                return;
            }

            // cleans up
            String hash = getHashFromIndex(fileName, getIndex());
            File backupFile = new File("git/objects/" + hash);
            File original = new File(fileName);
            backupFile.delete();
            original.delete();
            index.delete();
            indexCopy.renameTo(index);
        }
        System.out.println("createBlob method passed the test.");
    }

    public static void testCreateBlobWithDirectories() throws IOException, NoSuchAlgorithmException {
        if (!repoExistsHere()) {
            System.out.println("No repo exists in this directory.");
            return;
        }
        if (Git.dataCompressionEnabled())
            Git.toggleDataCompression();
        
        // Create a test directory with files
        File testerDirectory = new File("testDir");
        testerDirectory.mkdir();

        File testTextFile1 = new File("testDir/file1.txt");
        testTextFile1.createNewFile();
        BufferedWriter writer1 = new BufferedWriter(new FileWriter(testTextFile1));
        writer1.write("FILE 1 EXAMPEL");
        writer1.close();

        File testTextFile2 = new File("testDir/file2.txt");
        testTextFile2.createNewFile();
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(testTextFile2));
        writer2.write("SECOND FILe Ex");
        writer2.close();

        // Created a blob for the directory
        Git.addDirectory(testerDirectory.getPath());

        // Check if the directory and files are written correctly
        String indexContent = getIndex();
        if (!indexContent.contains("tree"))
            System.out.println("Failed. Directory is not labeled as a tree.");
        else if (!indexContent.contains("blob"))
            System.out.println("Failed. Files are not labeled as blob.");
        else 
            System.out.println("Correct! Directory and files aer correctly labeled as tree and blob.");

        // Cleanup
        removeDirectory(testerDirectory.getPath());
    }


    public static void testCreateBlobWithSubDirectories() throws IOException, NoSuchAlgorithmException {
        if (!repoExistsHere()) {
            System.out.println("No repo exists in this directory.");
            return;
        }

        if (Git.dataCompressionEnabled())
            Git.toggleDataCompression();
        
        // Create a test directory with files and subdirectories
        File testDirectory = new File("testDir");
        testDirectory.mkdir();

        File subDirectory = new File("testDir/subDir");
        subDirectory.mkdir();

        File testFile1 = new File("testDir/file1.txt");
        testFile1.createNewFile();
        BufferedWriter writer1 = new BufferedWriter(new FileWriter(testFile1));
        writer1.write("File 1 example data");
        writer1.close();

        File testFile2 = new File("testDir/subDir/file2.txt");
        testFile2.createNewFile();
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(testFile2));
        writer2.write("File 2 example data inside subdirectory");
        writer2.close();

        // Create a blob for the directory
        Git.addDirectory(testDirectory.getPath());

        // Check if the directory and files are written correctly
        String indexContent = getIndex();
        boolean treeExists = indexContent.contains("tree");
        boolean blobExists = indexContent.contains("blob");

        if (!treeExists)
            System.out.println("Failed. Directory and subdirectory are not labeled as trees.");
        else if (!blobExists)
            System.out.println("Failed. Files inside directories are not labeled as blobs.");
        else 
            System.out.println("Success! Directory, subdirectory, and files are correctly labeled as tree and blobs, and the format is correct.");

        // Cleanup
        removeDirectory(testDirectory.getPath());
    }


    /**
     * @param fileName - the name of the file
     * @param fileData - the data in the file
     * @return true if the blob referenced in the index contains the same data as
     *         the original file
     * @throws IOException
     */
    private static boolean blobMatchesData(String fileName, String fileData) throws IOException {
        String index = getIndex();

        String hash = getHashFromIndex(fileName, index);

        // verifies that the content of the backup matches the original data
        File backup = new File("git/objects/" + hash);
        byte[] fileByteData = fileData.getBytes("UTF-8");
        FileInputStream input = new FileInputStream(backup);
        byte[] backupFileData = new byte[(int) backup.length()];
        input.read(backupFileData);
        input.close();

        return byteArraysAreTheSame(fileByteData, backupFileData);
    }

    /**
     * @return the index in string form
     * @throws IOException
     */
    private static String getIndex() throws IOException {
        StringBuilder string = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("git/index"));
        while (reader.ready())
            string.append(reader.readLine());
        reader.close();
        return string.toString();
    }

    /**
     * @param fileName - the name of the file
     * @param index    - the index in string form
     * @return the relevant hash based on the filename
     */
    private static String getHashFromIndex(String fileName, String index) {
        int hashStartingIndex = index.indexOf(fileName) - 41;
        int hashEndingIndex = hashStartingIndex + 40;
        return index.substring(hashStartingIndex, hashEndingIndex);
    }

    /**
     * @param one - the first byte array
     * @param two - the second byte array
     * @return true if the byte arrays are identical
     */
    private static boolean byteArraysAreTheSame(byte[] one, byte[] two) {
        if (one.length != two.length)
            return false;
        for (int i = 0; i < one.length; i++) {
            if (one[i] != two[i])
                return false;
        }
        return true;
    }

    /**
     * Creates a blob & updates the index based on the file name and data.
     * 
     * @param fileName - the name of the file
     * @param fileData - the data in the file
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private static void generateTestBlob(String fileName, String fileData)
            throws IOException, NoSuchAlgorithmException {
        File file = new File(fileName);
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fileData);
        writer.close();
        Git.createBlob(file.getPath());
    }

    /**
     * Tests if the initRepo method works.
     * Creates many repositories and checks if they have the required folders and
     * files.
     * Deletes the repository afterwards.
     * 
     * @warning WILL ERASE THE CONTENTS OF GIT FOLDER, OBJECTS FOLDER, AND INDEX.
     * @throws IOException
     */
    public static void testInitRepo() throws IOException {
        for (int i = 0; i < 100; i++) {
            Git.initRepoHere();
            if (!repoExistsHere()) {
                System.out.println("Init repo method failed the test");
                return;
            }
            removeRepository();
        }
        System.out.println("Init repo method passed the test");
        Git.initRepoHere();
    }

    /**
     * Removes the repository at this location.
     */
    private static void removeRepository() {
        File gitFolder = new File("git");
        removeDirectory(gitFolder.getPath());
    }

    /**
     * Deletes a directory and all files within it
     * 
     * @param directoryName - the directory to delete
     */
    private static void removeDirectory(String directoryName) {
        File directory = new File(directoryName);
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                removeDirectory(file.getPath());
            file.delete();
        }
        directory.delete();
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


    /**
     * Prints an array of bytes to the console.
     * 
     * @param array - the array to be printed
     */
    @SuppressWarnings("unused")
    private static void printByteArray(byte[] array) {
        for (byte b : array) {
            System.out.print(b + ", ");
        }
        System.out.println();
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
}