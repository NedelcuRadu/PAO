import managers.AuctionManager;
import managers.UserManager;
import models.Product;
import models.User;

import java.util.List;
import java.util.Map;

public class BidParser {

    public static Product searchProduct(String auctionName, String productName)
    {
        var auction = AuctionManager.getInstance().findAuction(auctionName);
        return auction.findProduct(productName);
    }
    public static void populateBids(List<Map<String,String>> objs)
    {
        for (var obj : objs)
        { String userName = obj.get("BIDDER NAME");
            User bidder = UserManager.getInstance().findUser(userName);
        assert bidder!=null;
        String auctionName = obj.get("AUCTION NAME");
        String productName = obj.get("PRODUCT NAME");
        Product product = searchProduct(auctionName,productName);
        assert product!=null;
        Float amount = Float.parseFloat(obj.get("BID AMOUNT"));
        bidder.placeBid(product,amount);
        }
    }
}
