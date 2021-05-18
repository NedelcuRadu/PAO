package models;

import managers.AuctionManager;
import models.Auction;
import models.Product;
import models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Organizer extends User {
    private Map<String, Auction> auctionMapMap = new HashMap<>();

    public Organizer(String name, String password) {
        super(name, password);
    }

    public Organizer(String name, Date registerDate, Date birthDate, Float founds, String password) {
        super(name, registerDate, birthDate, founds, password);
    }

    public Organizer(String name, Date birthDate, Float founds, String password) {
        super(name, birthDate, founds, password);
    }

    public Organizer(String name, Date birthDate, String password) {
        super(name, birthDate, password);
    }

    public Product registerProduct(String name, Float startingPrice) {
        return new Product.ProductBuilder(name, this.getName()).withStartingPrice(startingPrice).build(); //Fac un produs
    }

    public Product registerProduct(String name, Float startingPrice, Float targetPrice) {
        return new Product.ProductBuilder(name, this.getName()).withTargetPrice(targetPrice).withStartingPrice(startingPrice).build(); //Fac un produs
    }

    public Auction createAuction(String name, Date endDate) {
        try {
            var newAuction = AuctionManager.getInstance().createAuction(this.getName(), name, endDate);
            auctionMapMap.put(newAuction.getName(), newAuction);
            return newAuction;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void showPanel() {
        super.showPanel();
        System.out.println("6. See your products.");
        System.out.println("7. Add a new product.");
        System.out.println("8. Create a new auction");
    }

    @Override
    public boolean checkCommand(Command command) {
        if (command == null)
            return false;
        return command.getValue() >= 0 && command.getValue() <= 8;
    }
}
