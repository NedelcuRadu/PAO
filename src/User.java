import java.util.*;

public class User{
    private String name; //Required
    private Date registerDate; //Required - default data apelarii
    private Date birthDate; //Required
    private Float founds = 0f;//Required, default 0
    private String passwordHash;
    private Map<Product, Bid> bidList = new HashMap<>(); // Bids placed
    private List<Product> productsList = new ArrayList<Product>(); // Produsele detinute

    protected User(String name, String password) {
        this.name = name;
        this.passwordHash = password;
        registerDate = new Date();
        birthDate = new Date();
    }

    public User(String name, Date registerDate, Date birthDate, Float founds, String password) {
        this.name = name;
        this.registerDate = registerDate;
        this.birthDate = birthDate;
        this.founds = founds;
        this.passwordHash = password;
    }

    public User(String name, Date birthDate, Float founds, String password) {
        this.name = name;
        this.registerDate = new Date();
        this.birthDate = birthDate;
        this.founds = founds;
        this.passwordHash = password;
    }

    public User(String name, Date birthDate, String password) {
        this.name = name;
        this.registerDate = new Date();
        this.birthDate = birthDate;
        this.founds = 0f;
        this.passwordHash = password; //TO DO: IMPLEMENT HASHING
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = password;
    }

    public String getName() {
        return this.name;
    }

    public Date getRegisterDate() {
        return (Date) this.registerDate.clone();
    }

    public Date getBirthDate() {
        return (Date) this.birthDate.clone();
    }

    public Float getFounds() {
        return this.founds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(Date date) {
        this.birthDate = (Date) date.clone();
    }

    public void setRegisterDate(Date date) {
        this.registerDate = (Date) date.clone();
    }

    public void setFounds(Float founds) {
        this.founds = founds;
    }

    public void addFounds(Float founds) {
        this.founds += founds;
    }

    public void showPanel() {
        System.out.println("0. Logout.");
        System.out.println("1. See auctions.");
        System.out.println("2. See your bids");
        System.out.println("3. Check your balance.");
        System.out.println("4. Add founds.");
        System.out.println("5. Exit app.");
    }

    public boolean checkCommand(Command command) {
        if (command == null)
            return false;

        if (command.getValue() <= 5 && command.getValue() >= 0)
            return true;
        return false;
    }

    public User command(Command command) {

        switch (command) {
            case EXIT:
                System.exit(0);
            case INDEXPRODUCTS: //See items
                this.indexProducts();
                break;
            case CHECKFOUNDS: //Check balance
                System.out.println("You currently have " + this.getFounds().toString() + "$ available.");
                break;
            case ADDFOUNDS:
                Scanner scanner = new Scanner(System.in);
                System.out.println("How much would you like to add?");
                float amount = scanner.nextFloat();
                this.addFounds(amount);
                break;
            case ADDPRODUCT:
                break;
            case INDEXUSERS:
                break;
            case CREATEAUCTION:
                break;
            case INDEXAUCTIONS: //See auctions
                AuctionManager.getInstance().index();
                break;
            case INDEXBIDS:
                this.indexBids();
                break;
            case LOGOUT:
                System.out.println("Logged out.");
                return null;
        }
        return this;
    }

    public void indexProducts() // TO DO
    {
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        System.out.format("|  ID  |  NAME  |  START PRICE  |  BIRTH DATE  |  FOUNDS  |%n");
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        for (int i = 0; i < productsList.size(); i++)
            System.out.println(productsList.get(i).toString());
        System.out.format("+------+--------+-----------------+--------------+----------+%n");
        for (var product : productsList)
            System.out.print(product);
    }

    public void indexBids() {
        System.out.format("+------------+-----------------+-------------+----------------+%n");
        System.out.format("|  ITEM NAME | PLACEMENT DATE  |    AMOUNT   |ARE YOU WINNING?|%n");
        System.out.format("+------------+-----------------+-------------+----------------+%n");
        for (var currentProduct : bidList.entrySet())
            currentProduct.getValue().showBid();
        System.out.format("+------------+-----------------+-------------+----------------+%n");
    }

    public void placeBid(Product product, Float amount) {
        if (amount < this.founds) {
            founds -= amount;
            Bid newBid = new Bid(this, amount, product);
            product.placeBid(newBid);
            bidList.put(product, newBid);
        } else
            System.out.println("You don't have enough founds, please add some first.");
    }

    public void placeBid(Product product, Float amount, Date date) {
        if (amount < this.founds) {
            founds -= amount;
            Bid newBid = new Bid(this, amount, date, product);
            product.placeBid(newBid);
            bidList.put(product, newBid);
        } else
            System.out.println("You don't have enough founds, please add some first.");
    }
    public void deleteBid(Product product)
    {
        bidList.remove(product);
    }

    @Override
    public String toString() {
        String leftAlignFormat = "| %-5s |   %te %<tb %<tY    |  %te %<tb %<tY  | %-4f |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, name, registerDate, birthDate, founds);

        return fmt.toString();
    }

}
