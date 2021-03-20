import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class AuctionManager implements Manager<Auction>{
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
    private AuctionManager(){}
    public static AuctionManager getInstance() {
        if (instance == null)
            instance = new AuctionManager();
        return instance;
    }
    public Auction createAuction(String organizer,String name, Date endDate) throws Exception {
        var auction = new Auction(organizer,name,endDate);
        auctions.add(auction);
        return auction;
    }
    public void index() {
        Collections.sort(auctions);
        System.out.format("+----+-----------------+------------+-------------+%n");
        System.out.format("| ID |       NAME      | START DATE |   END DATE  |%n");
        System.out.format("+----+-----------------+------------+-------------+%n");

        for (int i = 0; i < auctions.size(); i++)
            System.out.println(auctions.get(i).toString(i));
        System.out.format("+----+-----------------+------------+-------------+%n");
    }
    public void delete(Auction toDelete)
    {

        Function<Auction,Date> cmp = Auction::getStartDate;
        var index = binarySearch(auctions,toDelete.getStartDate(),cmp);
        if(auctions.get(index) == toDelete)
            auctions.remove(index);
        else
            System.out.println("De ce incerci sa stergi ceva ce nu exista?");
    }
}
