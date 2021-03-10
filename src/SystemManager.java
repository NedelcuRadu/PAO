import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SystemManager {
    private static SystemManager instance;
    private List<Auction> auctions = new ArrayList<Auction>();
    private Map<String, User> userMap = new HashMap<>();
    final static String DATE_FORMAT = "dd/MM/yyyy";

    public static Date convertToValidDate(String date) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private SystemManager() {
    }


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
    public static SystemManager getInstance() {
        if (instance == null)
            instance = new SystemManager();
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
    public Auction createAuction(String organizer,String name, Date endDate) throws Exception {
        var auction = new Auction(organizer,name,endDate);
        auctions.add(auction);
        return auction;
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

    public void indexUsers() {
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        System.out.format("|  ID  |  NAME  |  REGISTER DATE  |  BIRTH DATE  |  FOUNDS  |%n");
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        for (var user : userMap.entrySet())
            System.out.println(user.getValue());
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
    }

    public void indexAuctions() {
       Collections.sort(auctions);
        System.out.format("+----+-----------------+------------+-------------+%n");
        System.out.format("| ID |       NAME      | START DATE |   END DATE  |%n");
        System.out.format("+----+-----------------+------------+-------------+%n");
        for (int i = 0; i < auctions.size(); i++)
            System.out.println(auctions.get(i).toString(i));
        System.out.format("+----+-----------------+------------+-------------+%n");
    }
}

class SystemMain {
    public static void welcome() {
        System.out.println("Welcome to the SUPER Secure Auction System. Please log in or register.");
        System.out.println("0. Exit");
        System.out.println("1. Log In");
        System.out.println("2. Register");
    }

    public static void userPanel() {
        System.out.println("See auctions");
    }

    public static User readRegisterCredentials() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.next();
        while (SystemManager.getInstance().existsUser(username)) {
            System.out.println("Username taken, please try a different one.");
            System.out.print("Username: ");
            username = scanner.next();
        }
        String password1 = "ana", password2 = "ana2";
        Date birthdate = null;
        while (!password2.equals(password1)) {
            System.out.print("Password: ");
            password1 = scanner.next();
            System.out.print("Please confirm password by retyping it: ");
            password2 = scanner.next();
            if (!password2.equals(password1))
                System.out.println("Passwords don't match!");
        }
        while (birthdate == null) {
            System.out.print("Birthdate (DD/MM/YYYY): ");
            String date = scanner.next();
            birthdate = SystemManager.convertToValidDate(date);
            if (birthdate == null)
                System.out.println("Invalid date.");
        }
        return SystemManager.getInstance().createUser(username, birthdate, password1);
    }

    public static void main(String[] args) throws Exception {

        SystemManager manager = SystemManager.getInstance();
        var marian = manager.createUser("Marian", new Date(), "ana");
        var admin = manager.createAdmin("admin","admin");
        var organizer = manager.createOrganizer("London Museum",SystemManager.convertToValidDate("20/03/2021"),"org");
        var a1 =manager.createAuction("London Museum","Paris Paintings", SystemManager.convertToValidDate("23/02/2022"));
        manager.createAuction("London Museum","London Art", SystemManager.convertToValidDate("20/03/2021"));
        organizer.createAuction("Roman Treasures",SystemManager.convertToValidDate("22/03/2021"));
        Product p1 = new Product.ProductBuilder("Spear","Marian").build(); //Fac un produs
        a1.addProduct(p1); // Il adaug la licitatie
        marian.addFounds(300f);
        admin.addFounds(500f);
        admin.placeBid(p1,300f);
        marian.placeBid(p1,100f); // Fac un  bid pentru produs
        p1.buyOut(); //Termin licitatia pentru produs
        System.out.println(p1.getOwner()); //Afisez noul owner
        marian.indexBids(); //Acum nu mai are bids
        Scanner scanner = new Scanner(System.in);

        String username;
        String password;
        boolean flag = true;
        User loggedUser = null;
        while (true) {
            int command = -1;
            welcome();
            while (command<0 || command >3) {
                if(!flag)
                System.out.println("Unknown command!");
                flag = false;
                System.out.print("Your choice: ");
                command = scanner.nextInt();
            }

            while (loggedUser == null) {
                switch (command) {
                    case 0: return;
                    case 1: //LogIn
                        System.out.print("Username: ");
                        username = scanner.next();
                        System.out.print("Password: ");
                        password = scanner.next();
                        loggedUser = manager.logIn(username, password);
                        if (loggedUser == null)
                            System.out.println("There is no user with these credentials.");
                        break;
                    case 2:
                        loggedUser = readRegisterCredentials();
                        if (loggedUser == null)
                            System.out.println("There was a problem registering you account, please try again.");
                        break;
                }
            }
            while (loggedUser != null)
            //Acum am un cont logged in
            {
                System.out.println("Welcome, " + loggedUser.getName() + "!");
                loggedUser.showPanel();
                System.out.print("Your choice: ");
                command = scanner.nextInt();

                while (!loggedUser.checkCommand(Command.valueOf(command))) {
                    System.out.println("Unknown command!");
                    System.out.print("Your choice: ");
                    command = scanner.nextInt();
                }
                loggedUser = loggedUser.command(Command.valueOf(command));
            }
        } //Main while
    }
}

