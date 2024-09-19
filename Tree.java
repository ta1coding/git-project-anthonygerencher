import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Tree {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        // addFileToTree("test");
        // addFileToTree("test2");
        // addFileToTree("test3");
        // removeFileFromTree("test");
        // removeFileFromTree("test3");
    }

    /**
     * Removes a file from the tree. Does not delete the file, removes the hash from
     * the tree file.
     * 
     * @param fileName - file to be removed from the tree
     * @throws IOException
     */
    public static void removeFileFromTree(String fileName) throws IOException {
        File file = new File(fileName);
        File tree = new File("tree");

        // throws an exception if the file attempting to be removed doesn't exist or if
        // the tree itself doesn't exist
        if (!file.exists())
            throw new FileNotFoundException();
        if (!tree.exists())
            throw new FileNotFoundException("Cannot remove file because tree doesn't exist.");

        // makes a copy of the tree
        Files.copy(Path.of("tree"), Path.of("tree_copy"));

        // goes through the tree and doesn't write any lines that contain the file to be
        // removed
        BufferedReader reader = new BufferedReader(new FileReader("tree_copy"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree"));
        while (reader.ready()) {
            String line = reader.readLine();
            if (!Objects.equals(line.substring(line.length() - fileName.length()), fileName)) {
                writer.write(line);
                writer.newLine();
            }
        }

        // cleans up
        reader.close();
        writer.close();
        Files.delete(Path.of("tree_copy"));
    }

    /**
     * Creates a tree file here.
     * 
     * @throws IOException
     */
    public static void makeTreeFileHere() throws IOException {
        File tree = new File("tree");
        if (!tree.exists())
            tree.createNewFile();
    }

    /**
     * Adds a file to the tree at this directory in blob form.
     * 
     * @param pathToFile - the file to be added to the tree
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void addFileToTree(String pathToFile) throws NoSuchAlgorithmException, IOException {
        // ensures that the tree file exists before adding a blob
        makeTreeFileHere();

        // ensures that the blob version of the file exists in objects
        Blob.createBlob(pathToFile);

        // retrieves the hash from the index
        File file = new File(pathToFile);
        String fileName = file.getName();
        String index = getIndex();
        String hash = getHashFromIndex(fileName, index);
        String tree = getTree();
        // if the file is already in the tree, no need to continue
        if (tree.contains(fileName))
            return;
        // writes the data to the tree file
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree", true));
        writer.write("blob :  " + hash + " : " + fileName);
        writer.newLine();
        writer.close();
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
     * @return The tree in String form
     * @throws IOException
     */
    private static String getTree() throws IOException {
        StringBuilder string = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("tree"));
        while (reader.ready())
            string.append(reader.readLine());
        reader.close();
        return string.toString();
    }
}
