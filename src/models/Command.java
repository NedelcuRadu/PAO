package models;

import java.util.HashMap;
import java.util.Map;

public enum Command {
    LOGOUT(0),
    INDEXAUCTIONS(1),
    INDEXBIDS(2),
    CHECKFOUNDS(3),
    ADDFOUNDS(4),
    EXIT(5), //Pana aici pentru user normal
    INDEXPRODUCTS(6),
    ADDPRODUCT(7),
    CREATEAUCTION(8), //Pana aici pentru organiser
    INDEXUSERS(9);
    private int value;
    private static Map map = new HashMap<>();

    Command(int value) {
        this.value = value;
    }

    static {
        for (Command pageType : Command.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static Command valueOf(int pageType) {
        return (Command) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
