
import java.util.*;

public class Auction implements Comparable <Auction> {
    private static int id;
    private Date startDate = new Date(); //Default sysdate
    private Date endDate; //Required
    private List<Product> productList = new ArrayList<Product>();
    private final String name; //Required
    private Organizer organizer;
    static { id++; }

    @Override
    public int compareTo(Auction other) {
        Date date1 = this.getStartDate();
        Date date2 = other.getStartDate();
        if(date1.equals(date2))
            return this.getEndDate().compareTo(other.getEndDate());
        else
            return date1.compareTo(date2);
    }

    public Auction(String organizer,String name, Date startDate,Date endDate) {
        var foundUser = UserManager.getInstance().findUser(organizer);
        if(foundUser instanceof Organizer)
            this.organizer = (Organizer) UserManager.getInstance().findUser(organizer);

    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    }
    public Auction(Organizer organizer,String name, Date endDate)
    {
        this.name = name;
        this.endDate = endDate;
        this.organizer = organizer;
    }
    public Auction(String organizer, String name, Date endDate) {
        var foundUser = UserManager.getInstance().findUser(organizer);
        if(foundUser instanceof Organizer)
            this.organizer = (Organizer) UserManager.getInstance().findUser(organizer);
        else System.out.println("No organizer with name "+organizer);
        this.name = name;
        this.endDate = endDate;
    }
    public Date getStartDate() {
        return (Date) this.startDate.clone();
    }

    public void setStartDate(Date startDate) {
        this.startDate = (Date) this.startDate.clone();
    }

    public Date getEndDate() {
        return (Date) this.endDate.clone();
    }

    public void setEndDate(Date endDate) {
        this.endDate = (Date) endDate.clone();
    }

    public List<Product> getProducts() {
        return this.productList;
    }

    public String getName() {
        return name;
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
    }
    public void addProduct(Product product)
    {
        this.productList.add(product);
    }
    public void deleteProduct(Product product) {
                productList.remove(product);
    }
    public void closeAuction()
    {
        for(var temp : productList)
            temp.buyOut();
        productList.clear();
    }
    @Override
    public String toString()
    {
        String leftAlignFormat = "| %-2d | %-15s | %te %<tb %<tY | %te %<tb %<tY |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat,id,name,startDate,endDate);
        return fmt.toString();
    }
    public String toString(int index)
    {
        String leftAlignFormat = "| %-2d | %-15s | %te %<tb %<tY | %te %<tb %<tY |";
        Formatter fmt = new Formatter();
        fmt.format(leftAlignFormat,index,name,startDate,endDate);
        return fmt.toString();
    }
}
