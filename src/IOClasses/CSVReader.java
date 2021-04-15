package IOClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
