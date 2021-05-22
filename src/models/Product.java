package models;

import IOClasses.WriteToFile;
import managers.AuctionManager;
import managers.DBManager;
import managers.UserManager;
import validators.DataValidator;

import javax.xml.crypto.Data;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Product extends Model {

    public static class ProductBuilder {
        private Integer ID; //PK
        private String name; //REQUIRED
        private User owner; //REQUIRED
        private Float startPrice;
        private Float targetPrice;
        private String description;
        private Auction auction;
        private Float boughtPrice;

        public ProductBuilder(String name, String owner, float startPrice) {
            this.name = name;
            this.owner = UserManager.getInstance().findUser(owner);
            this.startPrice = startPrice;
        }


        public ProductBuilder withID(Integer id) {
            this.ID = id;
            return this;
        }

        public ProductBuilder withTargetPrice(Float targetPrice) {
            this.targetPrice = targetPrice;
            return this;  //By returning the builder each time, we can create a fluent interface.
        }

        public ProductBuilder withDescription(String desc) {
            this.description = desc;
            return this;
        }

        public ProductBuilder inAuction(Auction auction) {
            this.auction = auction;
            return this;
        }

        public ProductBuilder withBoughtPrice(Float boughtPrice) {
            this.boughtPrice = boughtPrice;
            return this;
        }

        public Product build() {

            Product product = new Product();
            product.startPrice = this.startPrice;
            product.targetPrice = this.targetPrice;
            product.owner = this.owner;

            product.description = this.description;
            product.auction = this.auction;
            product.name = this.name;
            product.id = this.ID;
            owner.addProduct(product);
            if (auction != null)
                auction.addProduct(product);
            return product;
        }
    }

    private Integer id;
    private Float startPrice;
    private Float targetPrice;
    private Float boughtPrice;
    private String name;
    private String description;
    private User owner;
    private Auction auction = null;
    PriorityQueue<Bid> bids = new PriorityQueue<Bid>(); // Un produs are mai multe bids, cel mai mare castiga
    // La final se curata si se schimba ownerul

    @Override
    public String getPK() {
        return id.toString();
    }

    @Override
    public void setPK(String pk) {
        this.id = Integer.parseInt(pk);
        DBManager.update(this);
    }

    @Override
    public String getTableName() {
        return "PRODUCTS";
    }

    @Override
    public String getInsertStatement() {
        StringBuilder stmt = new StringBuilder("INSERT INTO PRODUCTS VALUES (DEFAULT,");
        String auctionName = "NULL", targetPrice = "NULL", boughtPrice = "NULL", description = "NULL", owner;
        owner = getOwner().getPK();
        if (getAuction() != null)
            auctionName = getAuction().getPK();
        if (getTargetPrice() != null)
            targetPrice = getTargetPrice().toString();
        if (getBoughtPrice() != null)
            boughtPrice = getBoughtPrice().toString();
        if (getDescription() != null)
            description = DataValidator.escapeString(getDescription());
        stmt.append(auctionName).append(",");
        stmt.append(owner).append(",");
        stmt.append(DataValidator.escapeString(getName())).append(",");
        stmt.append(description).append(",");
        stmt.append(getStartPrice()).append(",");
        stmt.append(targetPrice).append(",");
        stmt.append(boughtPrice).append(")");
        return stmt.toString();
    }


    @Override
    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();
        values.put("ID", getPK());
        String auctionName = "NULL", targetPrice = "NULL", boughtPrice = "NULL", description = "NULL", owner;
        owner = getOwner().getPK();
        if (getAuction() != null)
            auctionName = getAuction().getPK();
        if (getTargetPrice() != null)
            targetPrice = getTargetPrice().toString();
        if (getBoughtPrice() != null)
            boughtPrice = getBoughtPrice().toString();
        if (getDescription() != null)
            description = DataValidator.escapeString(getDescription());
        values.put("AUCTION_ID", auctionName);
        values.put("OWNER", owner);
        values.put("NAME", DataValidator.escapeString(getName()));
        values.put("DESCRIPTION", description);
        values.put("START_PRICE", getStartPrice().toString());
        values.put("TARGET_PRICE", targetPrice);
        values.put("BOUGHT_PRICE", boughtPrice);
        return values;
    }

    @Override
    public void setInfo(Map<String, String> obj) {
        String name = obj.get("NAME");
        String owner = obj.get("OWNER");
        Integer id = Integer.parseInt(obj.get("ID"));
        String tmp = obj.get("TARGET_PRICE");
        String auctionName = obj.get("AUCTION_ID");
        Auction auction = null;
        if (auctionName != null) {
            auction = AuctionManager.getInstance().findAuction(auctionName);
            this.auction = auction;
        }
        Float targetPrice = null;
        Float boughtPrice = null;
        if (tmp != null)
            targetPrice = Float.parseFloat(tmp);
        tmp = obj.get("BOUGHT_PRICE");
        if (tmp != null)
            boughtPrice = Float.parseFloat(tmp);
        Float startingPrice = Float.parseFloat(obj.get("START_PRICE"));
        String description = obj.get("DESCRIPTION");
        this.description = description;
        this.name = name;
        this.owner = UserManager.getInstance().findUser(owner);
        this.startPrice = startingPrice;
        this.boughtPrice = boughtPrice;
        this.targetPrice = targetPrice;
        this.id = id;
    }

    private Product() {
    }

    public Bid getHighestBid() {
        WriteToFile.log();
        return bids.peek();
    }

    public void placeBid(Bid bid) {
        WriteToFile.log();
        bids.add(bid);
    }

    public void buyOut() {
        WriteToFile.log();
        if (bids.size() == 0)
            System.out.println("No bids for this item");
        this.owner = bids.peek().getOwner();
        this.boughtPrice = bids.peek().getAmount();
        while (bids.peek() != null) {
            var bid = bids.remove();
            var owner = bid.getOwner();
            owner.deleteBid(bid.getProduct());
        }
        if (this.auction != null)
            auction.deleteProduct(this); //Daca are un auction, il scot de pe lista de licitatie

    }

    public Float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Float startPrice) {
        this.startPrice = startPrice;
        DBManager.update(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        DBManager.update(this);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        DBManager.update(this);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        DBManager.update(this);
    }

    public void setId(Integer id) {
        this.id = id;
        DBManager.update(this);
    }

    public Float getBoughtPrice() {
        return boughtPrice;
    }

    public Float getTargetPrice() {
        return targetPrice;
    }

    public void setBoughtPrice(Float boughtPrice) {
        this.boughtPrice = boughtPrice;
        DBManager.update(this);
    }

    public void setTargetPrice(Float targetPrice) {
        this.targetPrice = targetPrice;
        DBManager.update(this);
    }

    public Auction getAuction() {
        return this.auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
        DBManager.update(this);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        String tPrice, bPrice;
        if (targetPrice == null)
            tPrice = "NONE";
        else
            tPrice = targetPrice.toString();
        if (boughtPrice == null)
            bPrice = "NONE";
        else
            bPrice = boughtPrice.toString();
        String leftAlignFormat = "| %-10s |   %-4f  |    %-10s  |    %-10s | %-10s |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat, name, startPrice, tPrice, bPrice, owner.getName());

        return fmt.toString();
        //return "Product: " + name + " Start Price: " + startPrice.toString() + " Target Price: " + tPrice + " Owned by: " + owner.getName();
    }
}
