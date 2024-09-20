import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class TreeTester {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        removeDirectory("git/objects");
    }

    //not yet working
    public static void cleanWorkspace () {
        File directory = new File("./");
        for (File file : directory.listFiles()) {
            if (file.getName().contains(".txt"))
                file.delete();
        }
        // removeDirectory("git/objects");
    }

    /**
     * Deletes a directory and all files within it
     * 
     * @param directoryName - the directory to delete
     */
    private static void removeDirectory(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists())
            return;
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                removeDirectory(file.getPath());
            file.delete();
        }
        directory.delete();
    }
}
