import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVReader {
    public static CSVReader instance;

    private CSVReader() {
    }

    ;

    public static CSVReader getInstance() {
        if (instance == null)
            instance = new CSVReader();
        return instance;
    }

    public static List<List<String>> read(String fileName, String delimiter) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            List<List<String>> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                result.add(Arrays.asList(values));
            }
            result.remove(0);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
