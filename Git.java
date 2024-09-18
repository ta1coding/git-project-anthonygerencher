import java.io.File;
import java.io.IOException;

public class Git {
    public static void main(String[] args) {

    }

    public static void initRepoHere() throws IOException {

        // Initiate requisite files
        File gitFolder = new File("git");
        File objectFolder = new File("git/objects");
        File indexFile = new File("git/index");

        //Checks if repository has already been created
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
}