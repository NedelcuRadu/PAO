import IOClasses.Parse;
import IOClasses.WriteToFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class UserManager implements Manager<User>, Parse<User> {
    private static UserManager instance;
    private Map<String, User> userMap = new ConcurrentHashMap<>(); //TO DO: Threads
    private UserManager(){}
    public User logIn(String username, String password) {
        var foundUser = userMap.get(username);
        if (foundUser != null)
            if (foundUser.getPasswordHash().equals(password)) //TO DO: HASHING
                return foundUser;
        return null;
    }

    @Override
    public User parse(List<String> obj) {
        User tmp =  new User(obj.get(0),DataValidator.convertToValidDate(obj.get(1)),DataValidator.convertToValidDate(obj.get(2)),Float.parseFloat(obj.get(3)),obj.get(4));
    return tmp;
    }

    public boolean existsUser(String username) {
        var foundUser = userMap.get(username);
        return foundUser != null;
    }
    public User findUser(String username)
    {
        return userMap.get(username);
    }
    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public User insert(User tmp)
    {
        userMap.put(tmp.getName(),tmp);
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
        var newUser = new User(name, birthDate,password);
        WriteToFile.log();
        return insert(newUser);
    }
    public Organizer createOrganizer(String name,Date birthDate,String password)
    {
        WriteToFile.log();
        var newUser = new Organizer(name, birthDate,password);
        userMap.put(name, newUser);
        return newUser;
    }
    public User createAdmin(String name, String password)
    {
        WriteToFile.log();
        var newAdmin = new Admin(name,password);
        userMap.put(name,newAdmin);
        return newAdmin;
    }

    public void index() {
        System.out.format("+--------+-----------------+--------------+----------+%n");
        System.out.format("|  NAME  |  REGISTER DATE  |  BIRTH DATE  |  FOUNDS  |%n");
        System.out.format("+--------+-----------------+--------------+----------+%n");
        BiConsumer<String,User> printConsumer = (key, value) -> {
            System.out.println(value);
        };
        userMap.forEach(printConsumer);
        System.out.format("+--------+-----------------+--------------+----------+%n");
        WriteToFile.log();
    }

    public void delete(User toDelete)
    {
        WriteToFile.log();
        userMap.remove(toDelete.getName());
    }



}
