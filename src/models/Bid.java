package models;

import managers.BidParser;
import managers.DBManager;
import managers.UserManager;
import validators.DataValidator;

import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class Bid extends Model implements Comparable<Bid> {
    private Float amount;
    private Date placementDate = new Date();
    private User owner;
    private Product product;
    private String ID;

    public void showBid() {
        String leftAlignFormat;
        if (product.getHighestBid() == this)
            leftAlignFormat = "| %-10s |   %te %<tb %<tY    | %-4f$ | HIGHEST BIDDER |";
        else
            leftAlignFormat = "| %-10s |   %te %<tb %<tY    | %-4f$ |                |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, product.getName(), placementDate, amount);
        System.out.println(fmt.toString());
    }

    public Bid(User whoBidded, Float amount, Product product) {
        this.owner = whoBidded;
        this.amount = amount;
        this.product = product;
        this.placementDate = new Date();
    }

    public Bid(User whoBidded, Float amount, Date date, Product product) {
        this.owner = whoBidded;
        this.amount = amount;
        this.placementDate = date;
        this.product = product;
    }

    public Bid(String ID, Date placementDate, Float amount, User whoBidded, Product product) {
        this.owner = whoBidded;
        this.amount = amount;
        this.placementDate = placementDate;
        this.product = product;
        this.ID = ID;
    }

    public Bid(Float amount) {
        this.amount = amount;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Date getPlacementDate() {
        return placementDate;
    }

    public User getOwner() {
        return owner;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public int compareTo(Bid bid) {
        if (this.getAmount() > bid.getAmount())
            return -1;
        return 1;
    }

    @Override
    public String getPK() {
        return DataValidator.escapeString(ID);
    }

    @Override
    public void setPK(String pk) {
        this.ID = pk;
    }

    @Override
    public String getInsertStatement() {
        StringBuilder stmt = new StringBuilder("INSERT INTO BIDS VALUES (DEFAULT,");
        String placementDate = DataValidator.escapeString(DataValidator.convertDateToString(getPlacementDate()));
        String amount = getAmount().toString();
        String owner = getOwner().getPK();
        String productID = product.getPK();
        stmt.append(placementDate).append(",");
        stmt.append(amount).append(",");
        stmt.append(owner).append(",");
        stmt.append(productID).append(")");
        return stmt.toString();
    }

    @Override
    public String getTableName() {
        return "BIDS";
    }

    @Override
    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();
        String formattedPlacDate = DataValidator.formatDateToString(getPlacementDate());

        values.put("ID", getPK());
        values.put("PLACEMENT_DATE", formattedPlacDate);
        values.put("AMOUNT", amount.toString());
        values.put("OWNER", owner.getPK());
        values.put("PRODUCT_ID", product.getPK());
        return values;
    }

    @Override
    public void setInfo(Map<String, String> info) {
        this.owner = UserManager.getInstance().findUser(info.get("OWNER"));
        this.product = BidParser.searchProduct(info.get("PRODUCT_ID"));
        this.ID = info.get("ID");
        this.amount = Float.parseFloat(info.get("AMOUNT"));
        this.placementDate = DataValidator.convertToValidDate(info.get("PLACEMENT_DATE"));
    }
}
