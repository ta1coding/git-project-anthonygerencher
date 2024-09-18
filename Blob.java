import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Blob {
    public static void main(String[] args) {
        
    }

    public static void createBlob (String pathToFile) throws NoSuchAlgorithmException, IOException {
        Git.createBlob(pathToFile);
    }
}
