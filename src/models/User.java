package models;

import IOClasses.WriteToFile;
import managers.AuctionManager;
import managers.DBManager;
import models.Command;
import validators.DataValidator;

import java.util.*;

public class User extends Model {
    private String name; //Required
    private Date registerDate; //Required - default data apelarii
    private Date birthDate; //Required
    private Float founds = 0f;//Required, default 0
    private String passwordHash;
    private Map<Product, Bid> bidList = new HashMap<>(); // Bids placed
    private List<Product> productsList = new ArrayList<Product>(); // Produsele detinute

    public User() {

    }

    public String getTableName() {
        return "USERS";
    }

    @Override
    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();
        String formattedRegDate = DataValidator.formatDateToString(getRegisterDate());
        String formattedBirthDate = DataValidator.formatDateToString(getBirthDate());
        System.out.println(formattedBirthDate + " " + formattedRegDate);
        values.put("ID", getPK());
        values.put("REG_DATE", formattedRegDate);
        values.put("BIRTH_DATE", formattedBirthDate);
        values.put("FOUNDS", founds.toString());
        values.put("PASSWORD", DataValidator.escapeString(passwordHash));
        return values;
    }

    @Override
    public void setInfo(Map<String, String> obj) {
        String name = obj.get("ID");
        Date registerDate = DataValidator.convertToValidDate(obj.get("REG_DATE"));
        Date birthDate = DataValidator.convertToValidDate(obj.get("BIRTH_DATE"));
        Float founds = Float.parseFloat(obj.get("FOUNDS"));
        String passwordHash = obj.get("PASSWORD");
        this.setPasswordHash(passwordHash);
        this.setFounds(founds);
        this.setBirthDate(birthDate);
        this.setRegisterDate(registerDate);
        this.setName(name);
    }

    public String getInsertStatement() {
        StringBuilder stmt = new StringBuilder("INSERT INTO USERS VALUES (");
        String registerDate = DataValidator.escapeString(DataValidator.convertDateToString(this.getRegisterDate()));
        String birthDate = DataValidator.escapeString(DataValidator.convertDateToString(this.getBirthDate()));
        stmt.append(DataValidator.escapeString(this.getName())).append(",");
        stmt.append(birthDate).append(",");
        stmt.append(this.getFounds().toString()).append(",");
        stmt.append(DataValidator.escapeString(this.getPasswordHash())).append(",");
        if (this.registerDate != null)
            stmt.append(registerDate).append(");");
        else
            stmt.append("DEFAULT);");
        return stmt.toString();
    }

    public void setPK(String name) {
        this.name = name;
        DBManager.update(this);
    }

    public String getPK() {
        return DataValidator.escapeString(name);
    }

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
        DBManager.update(this);
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
        DBManager.update(this);
    }

    public void setBirthDate(Date date) {
        this.birthDate = (Date) date.clone();
        DBManager.update(this);
    }

    public void setRegisterDate(Date date) {
        this.registerDate = (Date) date.clone();
        DBManager.update(this);

    }

    public void setFounds(Float founds) {
        this.founds = founds;
        DBManager.update(this);
    }

    public void addFounds(Float founds) {
        this.founds += founds;
        DBManager.update(this);
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
            case INDEXUSERS:
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

    public void indexProducts() {
        System.out.println("Products for " + name + ":");
        if (productsList.size() > 0) {
            System.out.format("+------------+---------------+----------------+----------------+-----------+%n");
            System.out.format("| ITEM NAME  |  START PRICE  |  TARGET PRICE  |  BOUGHT PRICE  |   OWNER   |%n");
            System.out.format("+------------+---------------+----------------+----------------+-----------+%n");
            for (Product product : productsList) System.out.println(product.toString());
            System.out.format("+------+--------+-----------------+--------------+----------+%n");
        } else
            System.out.println("None");
    }

    public void indexBids() {
        if (bidList.size() > 0) {
            System.out.format("+------------+-----------------+-------------+----------------+%n");
            System.out.format("|  ITEM NAME | PLACEMENT DATE  |    AMOUNT   |ARE YOU WINNING?|%n");
            System.out.format("+------------+-----------------+-------------+----------------+%n");
            for (var currentProduct : bidList.entrySet())
                currentProduct.getValue().showBid();
            System.out.format("+------------+-----------------+-------------+----------------+%n");
        } else
            System.out.println("No bids yet");
    }

    public void placeBid(Product product, Float amount) {
        WriteToFile.log();
        if (amount < this.founds) {

            founds -= amount;
            Bid newBid = new Bid(this, amount, product);
            product.placeBid(newBid);
            bidList.put(product, newBid);
            DBManager.insert(newBid);
        } else
            System.out.println("You don't have enough founds, please add some first.");
    }

    public void placeBid(Product product, Bid bid) //Pt bids citite din baza de date, au avut deja loc deci nu scad fonduri
    {
        WriteToFile.log();
        product.placeBid(bid);
        bidList.put(product, bid);
    }

    public Bid placeBidBD(Product product, Bid bid) {
        if (bid.getAmount() < this.founds) {
            this.founds -= bid.getAmount();
            bidList.put(product, bid);
            product.placeBid(bid);
            DBManager.update(this);
            return DBManager.insert(bid);
        } else
            System.out.println("You don't have enough founds, please add some first.");
        return null;
    }

    public void placeBid(Product product, Float amount, Date date) {
        WriteToFile.log();
        if (amount < this.founds) {
            founds -= amount;
            Bid newBid = new Bid(this, amount, date, product);
            product.placeBid(newBid);
            bidList.put(product, newBid);
            DBManager.insert(newBid);
        } else
            System.out.println("You don't have enough founds, please add some first.");
    }

    public void deleteBid(Product product) {
        WriteToFile.log();
        bidList.remove(product);
    }

    public void addProduct(Product product) {
        if (!productsList.contains(product))
            productsList.add(product);
    }

    public Product findProduct(String productID) {
        System.out.println("In user " + this.getName());
        System.out.println("Searching for: " + productID);
        return productsList.stream().filter(x -> {
            System.out.println(x.getPK());
            return x.getPK().equals(productID);
        }).findFirst().get();
    }

    @Override
    public String toString() {
        String leftAlignFormat = "| %-5s |   %te %<tb %<tY    |  %te %<tb %<tY  | %-4f |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, name, registerDate, birthDate, founds);

        return fmt.toString();
    }

}
