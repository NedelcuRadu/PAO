package models;

import java.util.Date;
import java.util.Formatter;

public class Bid implements Comparable<Bid> {
    private Float amount;
    private Date placementDate = new Date();
    private User owner;
    private Product product;

    public void showBid()
    {
        String leftAlignFormat;
        if(product.getHighestBid()==this)
            leftAlignFormat = "| %-10s |   %te %<tb %<tY    | %-4f$ | HIGHEST BIDDER |";
        else
            leftAlignFormat = "| %-10s |   %te %<tb %<tY    | %-4f$ |                |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat,product.getName(),placementDate,amount);
        System.out.println(fmt.toString());
    }
    public Bid(User whoBidded, Float amount, Product product) {
        this.owner = whoBidded;
        this.amount = amount;
        this.product = product;
    }
    public Bid(User whoBidded, Float amount, Date date, Product product) {
        this.owner = whoBidded;
        this.amount = amount;
        this.placementDate = date;
        this.product = product;
    }
    public Bid(Float amount)
    {
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
}
