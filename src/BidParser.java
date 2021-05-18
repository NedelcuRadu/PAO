import managers.AuctionManager;
import managers.UserManager;
import models.Product;
import models.User;

import java.util.List;

public class BidParser {
    //TO DO: SPLIT INTO BID MANAGER
    public static Product searchProduct(String auctionName, String productName)
    {
        var auction = AuctionManager.getInstance().findAuction(auctionName);
        return auction.findProduct(productName);
    }
    public static void populateBids(List<List<String>> objString)
    {
        for (var obj : objString)
        { User bidder = UserManager.getInstance().findUser(obj.get(0));
        assert bidder!=null;
        Product product = searchProduct(obj.get(1),obj.get(2));
        assert product!=null;
        Float amount = Float.parseFloat(obj.get(3));
        bidder.placeBid(product,amount);
        }
    }
}
