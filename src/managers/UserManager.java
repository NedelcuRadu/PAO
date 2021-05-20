package managers;

import IOClasses.Parse;
import IOClasses.WriteToFile;
import models.Admin;
import models.Organizer;
import models.User;
import validators.DataValidator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class UserManager implements Manager<User>, Parse<User> {
    private static UserManager instance;
    private Map<String, User> userMap = new ConcurrentHashMap<>(); //TO DO: Threads

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
        String name = obj.get("NAME");
        Date registerDate = DataValidator.convertToValidDate(obj.get("REG_DATE"));
        Date birthDate = DataValidator.convertToValidDate(obj.get("BIRTH_DATE"));
        Float founds = Float.parseFloat(obj.get("FOUNDS"));
        String passwordHash = obj.get("PASSWORD");
        User tmp = new User(name, registerDate, birthDate, founds, passwordHash);
        return tmp;
    }

    public boolean existsUser(String username) {
        var foundUser = userMap.get(username);
        return foundUser != null;
    }

    public User findUser(String id) {
        return userMap.get(id);
    }

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public User insert(User tmp) {
        tmp.setRegisterDate(new Date());
        tmp = DBManager.insert(tmp);
        if (tmp != null)
            userMap.put(tmp.getPK(), tmp);
        return tmp;
    }

    public User createUser(String name, Date registerDate, Date birthDate, Float founds, String password) {
        WriteToFile.log();
        var newUser = new User(name, registerDate, birthDate, founds, password);
        return insert(newUser);
    }

    public User createUser(String name, Date birthDate, Float founds, String password) {
        WriteToFile.log();
        var newUser = new User(name, birthDate, founds, password);
        return insert(newUser);
    }

    public User createUser(String name, Date birthDate, String password) {
        var newUser = new User(name, birthDate, password);
        WriteToFile.log();
        return insert(newUser);
    }

    public Organizer createOrganizer(String name, Date birthDate, String password) {
        WriteToFile.log();
        var newUser = new Organizer(name, birthDate, password);
        userMap.put(name, newUser);
        return newUser;
    }

    public User createAdmin(String name, String password) {
        WriteToFile.log();
        var newAdmin = new Admin(name, password);
        userMap.put(name, newAdmin);
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

    public void delete(User toDelete) {
        WriteToFile.log();
        userMap.remove(toDelete.getName());
        DBManager.delete(toDelete);
    }


}
