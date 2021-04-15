import IOClasses.WriteToFile;

import java.util.PriorityQueue;

public class Product {

    public static class ProductBuilder {
        private static int ID; //UNIQUE, pus automat
        private String name; //REQUIRED
        private String owner; //REQUIRED
        private Float startPrice;
        private Float targetPrice;
        private String description;
        private Auction auction;

        static {
            ID++;
        }

        public ProductBuilder(String name, String owner) {
            this.name = name;
            this.owner = owner;
        }

        public ProductBuilder withStartingPrice(Float startPrice) {
            this.startPrice = startPrice;
            return this;  //By returning the builder each time, we can create a fluent interface.
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


        public Product build() {
            //Here we create the actual bank account object, which is always in a fully initialised state when it's returned.
            Product product = new Product();  //Since the builder is in the BankAccount class, we can invoke its private constructor.
            product.startPrice = this.startPrice;
            product.targetPrice = this.targetPrice;
            product.owner = this.owner;
            product.description = this.description;
            product.auction = this.auction;
            product.name = this.name;
            product.id = this.ID;
            return product;
        }
    }

    private Integer id;
    private Float startPrice;
    private Float targetPrice;
    private Float boughtPrice;
    private String name;
    private String description;
    private String owner;
    private Auction auction = null;
    PriorityQueue<Bid> bids = new PriorityQueue<Bid>(); // Un produs are mai multe bids, cel mai mare castiga

    //La final se curata si se schimba ownerul
    private Product() {
    }

    ;

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
        if(bids.size()==0)
            System.out.println("No bids for this item");
        this.owner = bids.peek().getOwner().getName();
        this.boughtPrice = bids.peek().getAmount();
        while (bids.peek() != null) {
            var bid = bids.remove();
            var owner = bid.getOwner();
            owner.deleteBid(bid.getProduct());
        }
        if(this.auction!=null)
            auction.deleteProduct(this); //Daca are un auction, il scot de pe lista de licitatie

    }

    public Float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Float startPrice) {
        this.startPrice = startPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    @Override
    public int hashCode() {
        return id;
    }
    @Override
    public String toString() {
        return "Product: " + name + " Start Price: " + startPrice.toString() + " Target Price: " + targetPrice.toString() + " Owned by: " + owner;
    }
}
