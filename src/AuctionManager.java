import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;
    private List<Auction> auctions = new ArrayList<Auction>();

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
    public void indexAuctions() {
        Collections.sort(auctions);
        System.out.format("+----+-----------------+------------+-------------+%n");
        System.out.format("| ID |       NAME      | START DATE |   END DATE  |%n");
        System.out.format("+----+-----------------+------------+-------------+%n");
        for (int i = 0; i < auctions.size(); i++)
            System.out.println(auctions.get(i).toString(i));
        System.out.format("+----+-----------------+------------+-------------+%n");
    }
}
