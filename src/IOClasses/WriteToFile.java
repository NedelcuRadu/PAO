package IOClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

public class WriteToFile {
    private static WriteToFile instance;
    private static String whereToWrite;
    private WriteToFile(){};
    public static WriteToFile getInstance()
    {
        if (instance == null)
            instance = new WriteToFile();
        return instance;
    }
    public static void writeLn(String fileName, String msg)
    {
        try {
            FileWriter writer = new FileWriter(fileName,true);
            writer.write(msg+"\n");
            writer.close();
        } catch (FileNotFoundException e) {
            File file = createFile(fileName);
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(msg+"\n");
                writer.close();
            } catch (IOException ioException) {
                System.out.println("Error while trying to open the newly created file");
                ioException.printStackTrace();
            }

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    whereToWrite = fileName;
    }
    public static void log()
    {
        writeLn(whereToWrite,new Throwable().getStackTrace()[1]+","+new Timestamp(System.currentTimeMillis()));
    }
    private static File createFile(String fileName)
    {
        Path path = Paths.get(fileName);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        WriteToFile.writeLn("logging.txt","nume_actiune,timestamp");
        WriteToFile.log();
    }
}
