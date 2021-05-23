package managers;

import IOClasses.Parse;
import IOClasses.WriteToFile;
import models.Admin;
import models.Organizer;
import models.Product;
import models.User;
import validators.DataValidator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class UserManager implements Manager<User>, Parse<User> {
    private static UserManager instance;
    private Map<String, User> userMap = new HashMap<>(); //TO DO: Threads

    private UserManager() {
    }

    public User logIn(String username, String password) {
        var foundUser = userMap.get(username);
        if (foundUser != null)
            if (foundUser.getPasswordHash().equals(password)) //TO DO: HASHING
                return foundUser;
        return null;
    }

    @Override
    public User parse(Map<String, String> obj) {
        String name = obj.get("ID");
        Date registerDate = DataValidator.convertToValidDate(obj.get("REG_DATE"));
        Date birthDate = DataValidator.convertToValidDate(obj.get("BIRTH_DATE"));
        Float founds = Float.parseFloat(obj.get("FOUNDS"));
        String passwordHash = obj.get("PASSWORD");
        return new User(name, registerDate, birthDate, founds, passwordHash);
    }

    public boolean existsUser(String username) {
        var foundUser = userMap.get(username);
        return foundUser != null;
    }

    public User findUser(String id) {
        return userMap.get(DataValidator.escapeString(id));
    }

    public void promoteToOrganizer(String id) {
        User toPromote = findUser(id);
        if (toPromote == null) {
            System.out.println("Didn't find the user to promote");
            return;
        }
        userMap.remove(toPromote.getPK());
        Organizer tmp = new Organizer(toPromote.getName(), toPromote.getRegisterDate(), toPromote.getBirthDate(), toPromote.getFounds(), toPromote.getPasswordHash());
        userMap.put(tmp.getPK(), tmp);
    }


    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public User insert(User tmp) {
        if (tmp != null) {
            if (tmp.getRegisterDate() == null)
                tmp.setRegisterDate(new Date());
            userMap.put(tmp.getPK(), tmp);
        }
        return tmp;
    }

    public User createUser(String name, Date registerDate, Date birthDate, Float founds, String password) {
        WriteToFile.log();
        var newUser = new User(name, registerDate, birthDate, founds, password);
        DBManager.insert(newUser);
        return insert(newUser);
    }

    public User createUser(String name, Date birthDate, Float founds, String password) {
        WriteToFile.log();
        var newUser = new User(name, birthDate, founds, password);
        DBManager.insert(newUser);
        return insert(newUser);
    }

    public User createUser(String name, Date birthDate, String password) {
        var newUser = new User(name, birthDate, password);
        WriteToFile.log();
        DBManager.insert(newUser);
        return insert(newUser);
    }

    public Organizer createOrganizer(String name, Date birthDate, String password) {
        WriteToFile.log();
        System.out.println(birthDate);

        var newUser = new Organizer(name, new Date(), birthDate,0f, password);


        DBManager.insert(newUser);
        name = DataValidator.escapeString(name);
        userMap.put(name, newUser);
        return newUser;
    }


    public User createAdmin(String name, String password) {
        WriteToFile.log();
        var newAdmin = new Admin(name, password);
        userMap.put(name, newAdmin);
        DBManager.insert(newAdmin);
        return newAdmin;
    }

    public void index() {
        System.out.format("+--------+-----------------+--------------+----------+%n");
        System.out.format("|  NAME  |  REGISTER DATE  |  BIRTH DATE  |  FOUNDS  |%n");
        System.out.format("+--------+-----------------+--------------+----------+%n");
        BiConsumer<String, User> printConsumer = (key, value) -> {
            System.out.println(value);
        };
        userMap.forEach(printConsumer);
        System.out.format("+--------+-----------------+--------------+----------+%n");
        WriteToFile.log();
    }

    public void indexProducts() {
        BiConsumer<String, User> printConsumer = (key, value) -> {
            value.indexProducts();
        };
        userMap.forEach(printConsumer);
        WriteToFile.log();
    }

    public void indexBids() {
        BiConsumer<String, User> printConsumer = (key, value) -> {
            System.out.println("Bids for " + key);
            value.indexBids();
        };
        userMap.forEach(printConsumer);
        WriteToFile.log();
    }

    public void delete(User toDelete) {
        WriteToFile.log();
        userMap.remove(toDelete.getPK());
        DBManager.delete(toDelete);
    }

    public Product findProduct(String productID) {
        for (var user : userMap.values()) {
            var tmp = user.findProduct(productID);
            if (tmp != null)
                return tmp;
        }
        return null;
    }


}
