import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class UserManager implements Manager<User>{
    private static UserManager instance;
    private Map<String, User> userMap = new ConcurrentHashMap<>(); //TO DO: Threads

    public User logIn(String username, String password) {
        var foundUser = userMap.get(username);
        if (foundUser != null)
            if (foundUser.getPasswordHash().equals(password)) //TO DO: HASHING
                return foundUser;
        return null;
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

    public User createUser(String name, Date registerDate, Date birthDate, Float founds, String password) {
        var newUser = new User(name, registerDate, birthDate, founds, password);
        userMap.put(name, newUser);
        return newUser;
    }

    public User createUser(String name, Date birthDate, Float founds, String password) {
        var newUser = new User(name, birthDate, founds, password);
        userMap.put(name, newUser);
        return newUser;
    }

    public User createUser(String name, Date birthDate, String password) {
        var newUser = new User(name, birthDate,password);
        userMap.put(name, newUser);
        return newUser;
    }
    public Organizer createOrganizer(String name,Date birthDate,String password)
    {
        var newUser = new Organizer(name, birthDate,password);
        userMap.put(name, newUser);
        return newUser;
    }
    public User createAdmin(String name, String password)
    {
        var newAdmin = new Admin(name,password);
        userMap.put(name,newAdmin);
        return newAdmin;
    }

    public void index() {
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        System.out.format("|  ID  |  NAME  |  REGISTER DATE  |  BIRTH DATE  |  FOUNDS  |%n");
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        BiConsumer<String,User> method = (key, value) -> {
            System.out.println(value);
        };
        userMap.forEach(method);
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
    }

    public void delete(User toDelete)
    {
        userMap.remove(toDelete.getName());
    }



}
