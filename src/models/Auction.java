package models;

import IOClasses.WriteToFile;
import managers.AuctionManager;
import managers.DBManager;
import managers.UserManager;
import validators.DataValidator;

import java.util.*;

public class Auction extends Model implements Comparable<Auction> {
    private Date startDate = new Date(); //Default sysdate
    private Date endDate; //Required
    private List<Product> productList = new ArrayList<Product>();
    private String name; //Required
    private Organizer organizer;


    @Override
    public int compareTo(Auction other) {
        Date date1 = this.getStartDate();
        Date date2 = other.getStartDate();
        if (date1.equals(date2))
            return this.getEndDate().compareTo(other.getEndDate());
        else
            return date1.compareTo(date2);
    }

    public Auction(String organizer, String name, Date startDate, Date endDate) {
        var foundUser = UserManager.getInstance().findUser(organizer);
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        if (foundUser instanceof Organizer) {
            this.organizer = (Organizer) foundUser;
            ((Organizer) foundUser).addAuction(this);
        } else System.out.println("No organizer with name " + organizer);
    }

    public Auction(Organizer organizer, String name, Date endDate) {
        this.name = name;
        this.endDate = endDate;
        this.organizer = organizer;
        organizer.addAuction(this);
    }

    public Auction(String organizer, String name, Date endDate) {

        var foundUser = UserManager.getInstance().findUser(organizer);
        System.out.println(foundUser);
        this.name = name;
        this.endDate = endDate;
        if (foundUser instanceof Organizer) {
            this.organizer = (Organizer) foundUser;
            ((Organizer) foundUser).addAuction(this);
        } else System.out.println("No organizer with name " + organizer);
    }

    public Date getStartDate() {
        return (Date) this.startDate.clone();
    }

    public void setStartDate(Date startDate) {
        this.startDate = (Date) this.startDate.clone();
        DBManager.update(this);
    }

    public Date getEndDate() {
        return (Date) this.endDate.clone();

    }

    public void setEndDate(Date endDate) {
        this.endDate = (Date) endDate.clone();
        DBManager.update(this);
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
        DBManager.update(this);
    }

    public void setOrganizer(String organizer_id) {
        var foundUser = UserManager.getInstance().findUser(organizer_id);
        if (foundUser instanceof Organizer) {
            this.organizer = (Organizer) foundUser;
            DBManager.update(this);
        } else System.out.println("No organizer with name " + organizer);
    }

    public String getOrganizerId() {
        return organizer.getPK();
    }

    public List<Product> getProducts() {
        return this.productList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        DBManager.update(this);
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
    }

    public void addProduct(Product product) {
        WriteToFile.log();
        this.productList.add(product);
    }

    public void deleteProduct(Product product) {
        WriteToFile.log();
        productList.remove(product);
        // product.setAuction("NULL");
    }

    public void closeAuction() {
        for (var temp : productList)
            temp.buyOut();
        productList.clear();
    }

    @Override
    public String toString() {
        String leftAlignFormat = "| %-15s | %te %<tb %<tY | %te %<tb %<tY |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, name, startDate, endDate);
        return fmt.toString();
    }

    public String toString(int index) {
        String leftAlignFormat = "| %-15s | %-15s | %te %<tb %<tY | %te %<tb %<tY |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, organizer.getPK(), name, startDate, endDate);
        return fmt.toString();
    }

    public void indexProducts() {
        for (var product : productList) {
            System.out.println(product);
        }
    }

    public Product findProduct(String productID) {
        System.out.println("In auction: " + getName());
        for (var product : productList) {
            System.out.println(productID + " " + product.getPK());
            if (productID.equals(product.getPK()))
                return product;
        }
        return null;
    }

    @Override
    public String getPK() {
        return DataValidator.escapeString(name);
    }

    @Override
    public void setPK(String pk) {
        this.name = pk;
    }

    @Override
    public String getInsertStatement() {
        StringBuilder stmt = new StringBuilder("INSERT INTO AUCTIONS VALUES (");
        String formattedName = DataValidator.escapeString(getName());
        String startDate = DataValidator.formatDateToString(getStartDate());
        String endDate = DataValidator.formatDateToString(getEndDate());
        stmt.append(formattedName).append(",");
        stmt.append(startDate).append(",");
        stmt.append(endDate).append(",");
        stmt.append(getOrganizerId()).append(")");
        System.out.println(stmt.toString());
        return stmt.toString();
    }

    @Override
    public String getTableName() {
        return "AUCTIONS";
    }

    @Override
    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();
        String formattedStartDate = DataValidator.formatDateToString(getStartDate());
        String formattedEndDate = DataValidator.formatDateToString(getEndDate());
        values.put("ID", getPK());
        values.put("START_DATE", formattedStartDate);
        values.put("END_DATE", formattedEndDate);
        values.put("USER_ID", organizer.getPK());
        return values;
    }

    @Override
    public void setInfo(Map<String, String> obj) {
        String organizer = obj.get("USER_ID");
        String auctionName = obj.get("ID");
        Date startDate = DataValidator.convertToValidDate(obj.get("START_DATE"));
        Date endDate = DataValidator.convertToValidDate(obj.get("END_DATE"));
        var foundUser = UserManager.getInstance().findUser(organizer);
        if (foundUser instanceof Organizer)
            this.organizer = (Organizer) foundUser;
        this.name = auctionName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
