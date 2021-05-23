package managers;

import models.Bid;
import models.Product;
import models.User;
import validators.DataValidator;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BidParser {

    public static Product searchProduct(String productID) {
        return UserManager.getInstance().findProduct(productID); //Caut in toate produsele
    }

    public static void populateBids(List<Map<String, String>> objs) {
        for (var obj : objs) {
            String userName = obj.get("OWNER");
            User bidder = UserManager.getInstance().findUser(userName);
            assert bidder != null;
            String productId = obj.get("PRODUCT_ID");
            String bidId = obj.get("ID");
            Product product = searchProduct(productId);
            Date placementDate = DataValidator.convertToValidDate(obj.get("PLACEMENT_DATE"));
            assert product != null;
            Float amount = Float.parseFloat(obj.get("AMOUNT"));
            Bid bid = new Bid(bidId, placementDate, amount, bidder, product);
            bidder.placeBid(product, bid);
        }
    }
}
