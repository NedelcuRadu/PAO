package models;

import IOClasses.WriteToFile;
import managers.AuctionManager;
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
        if (foundUser instanceof Organizer)
            this.organizer = (Organizer) UserManager.getInstance().findUser(organizer);

        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Auction(Organizer organizer, String name, Date endDate) {
        this.name = name;
        this.endDate = endDate;
        this.organizer = organizer;
    }

    public Auction(String organizer, String name, Date endDate) {
        var foundUser = UserManager.getInstance().findUser(organizer);
        if (foundUser instanceof Organizer)
            this.organizer = (Organizer) foundUser;
        else System.out.println("No organizer with name " + organizer);
        this.name = name;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return (Date) this.startDate.clone();
    }

    public void setStartDate(Date startDate) {
        this.startDate = (Date) this.startDate.clone();
    }

    public Date getEndDate() {
        return (Date) this.endDate.clone();
    }

    public void setEndDate(Date endDate) {
        this.endDate = (Date) endDate.clone();
    }
    public void setOrganizer(Organizer organizer)
    {
        this.organizer =  organizer;
    }
    public void setOrganizer(String organizer_id)
    {
        var foundUser = UserManager.getInstance().findUser(organizer_id);
        if (foundUser instanceof Organizer)
            this.organizer = (Organizer) foundUser;
        else System.out.println("No organizer with name " + organizer);
    }
    public String getOrganizerId()
    {
        return organizer.getPK();
    }
    public List<Product> getProducts() {
        return this.productList;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){this.name = name;}
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
        String leftAlignFormat = "| %-2d | %-15s | %te %<tb %<tY | %te %<tb %<tY |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, index, name, startDate, endDate);
        return fmt.toString();
    }

    public void indexProducts() {
        for (var product : productList) {
            System.out.println(product);
        }
    }
    public Product findProduct(String productName)
    {
        for(var product:productList)
            if (productName.equals(product.getName()))
                return product;
            return null;
    }

    @Override
    public String getPK() {
        return name;
    }

    @Override
    public void setPK(String pk) {
        this.name = pk;
    }

    @Override
    public String getInsertStatement() {
        StringBuilder stmt =  new StringBuilder("INSERT INTO AUCTIONS VALUES (DEFAULT,");
        String formattedName = DataValidator.escapeString(getName());
        String startDate = DataValidator.formatDateToString(getStartDate());
        String endDate = DataValidator.formatDateToString(getEndDate());
        stmt.append(formattedName).append(",");
        stmt.append(startDate).append(",");
        stmt.append(endDate).append(",");
        stmt.append(getOrganizerId());
        System.out.println(stmt.toString());
        return stmt.toString();
    }

    @Override
    public String getTableName() {
        return "AUCTIONS";
    }

    @Override
    public Map<String, String> getValues() {
        Map<String,String> values = new HashMap<>();
        String formattedStartDate = DataValidator.formatDateToString(getStartDate());
        String formattedEndDate = DataValidator.formatDateToString(getEndDate());
        values.put("ID",getPK());
        values.put("NAME",DataValidator.escapeString(name));
        values.put("START_DATE",formattedStartDate);
        values.put("END_DATE",formattedEndDate);
        values.put("USER_ID",organizer.getPK());
        return values;
    }

    @Override
    public void setInfo(Map<String, String> obj) {
        String organizer = obj.get("ORGANIZER");
        String auctionName = obj.get("AUCTION_NAME");
        Date startDate = DataValidator.convertToValidDate(obj.get("START_DATE"));
        Date endDate = DataValidator.convertToValidDate(obj.get("END_DATE"));
        setOrganizer(organizer);
        setName(auctionName);
        setStartDate(startDate);
        setEndDate(endDate);
    }

}