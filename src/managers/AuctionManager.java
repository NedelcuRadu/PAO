package managers;

import IOClasses.Parse;
import IOClasses.WriteToFile;
import models.Auction;
import models.Product;
import models.User;
import validators.DataValidator;

import java.util.*;
import java.util.function.Function;

public class AuctionManager implements Manager<Auction>, Parse<Auction> {
    private static AuctionManager instance;
    private List<Auction> auctions = new ArrayList<Auction>(); //Sunt sortate in ordine crescatoare dupa StartDate

    /**
     * Returns the index of the biggest value in the collection smaller than the given one, computed by the given function
     *
     * @param collection a collection of objects
     * @param value      the searched value
     * @param function   a function that when supplied with an object returns a comparable value
     * @return int    the index
     */
    private <obj, val extends Comparable<val>> int binarySearch(List<obj> collection, val value, Function<obj, val> function) {
        int i, step;
        int n = collection.size();
        for (step = 1; step <= n; step <<= 1) ;
        for (i = 0; step > 0; step >>= 1)
            if (i + step < n && function.apply(collection.get(i + step)).compareTo(value) < 0) //Daca e in limita array-ului si mai mic sau egal decat valoarea cautata
                i += step; //Crestem indicele
        return i + 1;
    }

    @Override
    public Auction parse(Map<String, String> obj) {
        String organizer = obj.get("USER_ID");
        UserManager.getInstance().promoteToOrganizer(organizer);
        String auctionName = obj.get("ID");
        Date startDate = DataValidator.convertToValidDate(obj.get("START_DATE"));
        Date endDate = DataValidator.convertToValidDate(obj.get("END_DATE"));
        return new Auction(organizer, auctionName, startDate, endDate);
    }

    public Product parseProduct(Map<String, String> obj) {
        String name = obj.get("NAME");
        String owner = obj.get("OWNER");
        Integer id = Integer.parseInt(obj.get("ID"));
        String tmp = obj.get("TARGET_PRICE");
        Float targetPrice = null;
        Float boughtPrice = null;
        if (tmp!=null)
           targetPrice = Float.parseFloat(tmp);
        tmp = obj.get("BOUGHT_PRICE");
        if (tmp!=null)
            boughtPrice = Float.parseFloat(tmp);
        Float startingPrice = Float.parseFloat(obj.get("START_PRICE"));
        String description = obj.get("DESCRIPTION");
        return new Product.ProductBuilder(name, owner,startingPrice).withID(id).withTargetPrice(targetPrice).withDescription(description).withBoughtPrice(boughtPrice).build();
    }

    public void populateProducts(List<Map<String, String>> objs) {
        for (var productMap : objs) {
            var auctionName = productMap.get("AUCTION_ID");
            var ownerID = productMap.get("OWNER");
            var parsedProduct = parseProduct(productMap);
            if (auctionName!=null)
            findAuction(auctionName).addProduct(parsedProduct);
            UserManager.getInstance().findUser(ownerID).addProduct(parsedProduct);
        }
    }

    public Auction insert(Auction auc) {
        auctions.add(auc);
        return auc;
    }

    private AuctionManager() {
    }

    public static AuctionManager getInstance() {
        if (instance == null)
            instance = new AuctionManager();
        return instance;
    }

    public Auction createAuction(String organizer, String name, Date endDate) {
        WriteToFile.log();
        var auction = new Auction(organizer, name, endDate);
        var insertedAuction = DBManager.insert(auction);
        if (insertedAuction != null)
            return insert(insertedAuction);

        return null;
    }

    public Auction findAuction(String name) {
        System.out.println("Searching for "+name);
        for (var tmp : auctions)
            if (tmp.getName().equals(name))
                return tmp;

        return null;
    }

    public Auction createAuction(String organizer, String name, Date startDate, Date endDate) {
        WriteToFile.log();
        var auction = new Auction(organizer, name, startDate, endDate);
        var insertedAuction = DBManager.insert(auction);
        if (insertedAuction != null)
            return insert(insertedAuction);

        return null;
    }

    public void index() {
        Collections.sort(auctions);
        System.out.format("+-------------+-----------------+------------+-------------+%n");
        System.out.format("|  ORGANIZER  |       NAME      | START DATE |   END DATE  |%n");
        System.out.format("+-------------+-----------------+------------+-------------+%n");

        for (int i = 0; i < auctions.size(); i++)
            System.out.println(auctions.get(i).toString(i));
        System.out.format("+----+-----------------+------------+-------------+%n");
        WriteToFile.log();
    }

    public void delete(Auction toDelete) {
        if (toDelete==null)
            return;
        Function<Auction, Date> cmp = Auction::getStartDate;
        var index = binarySearch(auctions, toDelete.getStartDate(), cmp);
        if (auctions.get(index) == toDelete) {
            auctions.remove(index);
            DBManager.delete(toDelete);
        } else
            System.out.println("De ce incerci sa stergi ceva ce nu exista?");
        WriteToFile.log();
    }

    public void indexProducts() {
        for (int i = 0; i < auctions.size(); i++)
            auctions.get(i).indexProducts();
    }

}
