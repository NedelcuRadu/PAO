import IOClasses.Parse;
import IOClasses.WriteToFile;

import java.util.*;
import java.util.function.Function;

public class AuctionManager implements Manager<Auction>, Parse<Auction> {
    private static AuctionManager instance;
    private List<Auction> auctions = new ArrayList<Auction>(); //Sunt sortate in ordine crescatoare dupa StartDate

    /**
     * Returns the index of the biggest value in the collection smaller than the given one, computed by the given function
     * @param  collection a collection of objects
     * @param  value the searched value
     * @param  function a function that when supplied with an object returns a comparable value
     * @return int    the index
     */
    private <obj,val extends Comparable<val>> int binarySearch(List<obj> collection, val value, Function<obj,val> function)
    {
        int i,step;
        int n = collection.size();
        for(step=1;step<=n;step<<=1);
        for(i=0; step>0; step>>=1)
            if(i+step<=n && function.apply(collection.get(i+step)).compareTo(value)<0) //Daca e in limita array-ului si mai mic sau egal decat valoarea cautata
                i+=step; //Crestem indicele
        return i+1;
    }

    @Override
    public Auction parse(List<String> obj) {
        return new Auction(obj.get(0),obj.get(1),DataValidator.convertToValidDate(obj.get(2)),DataValidator.convertToValidDate(obj.get(3)));
    }
    public Product parseProduct(List<String> obj)
    {
        return new Product.ProductBuilder(obj.get(0),obj.get(1)).withStartingPrice(Float.parseFloat(obj.get(2))).withTargetPrice(Float.parseFloat(obj.get(3))).withDescription(obj.get(4)).build();
    }
    public void populateProducts(List<List<String>> obj)
    {
        for (var productString: obj) {
            findAuction(productString.get(5)).addProduct(parseProduct(productString));
        }
    }
    public Auction insert(Auction auc)
    {
        auctions.add(auc);
        return auc;
    }

    private AuctionManager(){}
    public static AuctionManager getInstance() {
        if (instance == null)
            instance = new AuctionManager();
        return instance;
    }
    public Auction createAuction(String organizer,String name, Date endDate) {
        WriteToFile.log();
        var auction = new Auction(organizer,name,endDate);
       return insert(auction);
    }
    public Auction findAuction(String name)
    {
        for(var tmp : auctions)
            if (tmp.getName().equals(name))
                return tmp;

            return null;
    }
    public Auction createAuction(String organizer,String name, Date startDate,Date endDate) {
        WriteToFile.log();
        var auction = new Auction(organizer,name,startDate,endDate);
        return insert(auction);
    }
    public void index() {
        Collections.sort(auctions);
        System.out.format("+----+-----------------+------------+-------------+%n");
        System.out.format("| ID |       NAME      | START DATE |   END DATE  |%n");
        System.out.format("+----+-----------------+------------+-------------+%n");

        for (int i = 0; i < auctions.size(); i++)
            System.out.println(auctions.get(i).toString(i));
        System.out.format("+----+-----------------+------------+-------------+%n");
        WriteToFile.log();
    }
    public void delete(Auction toDelete)
    {

        Function<Auction,Date> cmp = Auction::getStartDate;
        var index = binarySearch(auctions,toDelete.getStartDate(),cmp);
        if(auctions.get(index) == toDelete)
            auctions.remove(index);
        else
            System.out.println("De ce incerci sa stergi ceva ce nu exista?");
        WriteToFile.log();
    }
    public void indexProducts()
    {
        for (int i = 0; i < auctions.size(); i++)
           auctions.get(i).indexProducts();
    }

}
