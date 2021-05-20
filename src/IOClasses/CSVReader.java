package IOClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    public static List<Map<String,String>> read(String fileName, String delimiter) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            List<Map<String,String>> result = new ArrayList<>();
            boolean headerRow = true;
            List<String> props = null;
            String line;
            while ((line = br.readLine()) != null) {
                if (headerRow)
                {
                    line = line.toUpperCase(Locale.ROOT);
                    headerRow = false;
                    props = Arrays.asList(line.split(delimiter));
                }
                else
                {
                    String[] values = line.split(delimiter);
                    Map<String,String> propsMap = new HashMap<String, String>();
                    for (int i = 0; i < props.size(); i++) {
                        propsMap.put(props.get(i),values[i]);
                    }
                    result.add(propsMap);
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
