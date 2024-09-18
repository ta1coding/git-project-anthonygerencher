import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

   
    /**
     * Adds a file to the tree at this directory in blob form.
     * 
     * @param pathToFile - the file to be added to the tree
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void addBlobToTree (String pathToFile) throws NoSuchAlgorithmException, IOException {
        //ensures that the tree file exists before adding a blob
        makeTreeFileHere();
        
        //ensures that the blob version of the file exists in objects
        Blob.createBlob(pathToFile);

        //retrieves the hash from the index
        File file = new File(pathToFile);
        String index = getIndex();
        String hash = getHashFromIndex(file.getName(), index);
        String tree = getTree();
        //if the blob is already in the tree, no need to continue
        if (tree.contains(hash))
            return;
        //writes the data to the tree file
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree", true));
        writer.write("blob :  " + hash + " : " + file.getName());
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
    private static String getTree () throws IOException {
        StringBuilder string = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("tree"));
        while (reader.ready())
            string.append(reader.readLine());
        reader.close();
        return string.toString();
    }
}
