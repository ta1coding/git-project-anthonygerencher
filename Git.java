import java.io.File;
import java.io.IOException;

public class Git {
    public static void main(String[] args) {

    }

    public static void initRepo(String directoryName) throws IOException {

        // Initiate requisite files
        File directory = new File(directoryName);
        File gitFolder = new File(directoryName + "/git");
        File objectFolder = new File(directoryName + "/git/objects");
        File indexFile = new File(directoryName + "/git/index");

        //Checks if repository has already been created
        // if (directory.exists() && gitFolder.exists() && objectFolder.exists() && indexFile.exists()) {
        //     System.out.println("Git Repository already exists");
        // } 

        // Create requisite directories and files if they don't already exist
        // else {
            if (!directory.exists())
                directory.mkdir();
            if (!gitFolder.exists())
                gitFolder.mkdir();
            if (!objectFolder.exists())
                objectFolder.mkdirs();
            if (!indexFile.exists())
                indexFile.createNewFile();
        // }

    }
}