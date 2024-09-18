import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Tree {
    public static void main(String[] args) {

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

    //TODO add blobs to the tree
    public static void addBlobToTree (String pathToFile) throws NoSuchAlgorithmException, IOException {
        Blob.createBlob(pathToFile);

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
}
