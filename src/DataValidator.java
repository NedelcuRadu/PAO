import IOClasses.WriteToFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataValidator {
    final static String DATE_FORMAT = "dd/MM/yyyy";

    public static Date convertToValidDate(String date) {
        WriteToFile.log();
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}

