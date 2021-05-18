import IOClasses.CSVReader;
import IOClasses.WriteToFile;
import databaseConfig.UserDatabaseManager;
import managers.AuctionManager;
import managers.UserManager;
import models.Command;
import models.Product;
import models.User;
import validators.DataValidator;

import java.util.Date;
import java.util.Scanner;
public class Main {

        public static void welcome() {
            System.out.println("Welcome to the SUPER Secure Models.Auction System. Please log in or register.");
            System.out.println("0. Exit");
            System.out.println("1. Log In");
            System.out.println("2. Register");
        }

        public static void userPanel() {
            System.out.println("See auctions");
        }

        public static User readRegisterCredentials() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Username: ");
            String username = scanner.next();
            while (UserManager.getInstance().existsUser(username)) {
                System.out.println("Username taken, please try a different one.");
                System.out.print("Username: ");
                username = scanner.next();
            }
            String password1 = "ana", password2 = "ana2";
            Date birthdate = null;
            while (!password2.equals(password1)) {
                System.out.print("Password: ");
                password1 = scanner.next();
                System.out.print("Please confirm password by retyping it: ");
                password2 = scanner.next();
                if (!password2.equals(password1))
                    System.out.println("Passwords don't match!");
            }
            while (birthdate == null) {
                System.out.print("Birthdate (DD/MM/YYYY): ");
                String date = scanner.next();
                birthdate = DataValidator.convertToValidDate(date);
                if (birthdate == null)
                    System.out.println("Invalid date.");
            }
            return UserManager.getInstance().createUser(username, birthdate, password1);
        }

        public static void main(String[] args) throws Exception {
            WriteToFile.writeLn("logging.txt","nume_actiune,timestamp");
            AuctionManager auctionManager = AuctionManager.getInstance();
            UserManager userManager = UserManager.getInstance();
            //Citesc users din CSV
            var userStrings = CSVReader.read("users.csv",",");
            assert userStrings != null;
            userManager.parseList(userStrings);

            var marian = userManager.createUser("Marian", new Date(), "ana");
            UserDatabaseManager userDatabsae = new UserDatabaseManager();
            userDatabsae.insert(marian);
            var admin = userManager.createAdmin("admin","admin");
            var organizer = userManager.createOrganizer("London Museum",DataValidator.convertToValidDate("20/03/2021"),"org");
            var a1 =auctionManager.createAuction("London Museum","Paris Paintings", DataValidator.convertToValidDate("23/02/2022"));

            //Citesc auctions din CSV
            var auctionsStrings = CSVReader.read("auctions.csv",",");
            assert auctionsStrings != null;
            auctionManager.parseList(auctionsStrings);
            organizer.createAuction("Roman Treasures", DataValidator.convertToValidDate("22/03/2021"));
            var productStrings = CSVReader.read("ProductsCSV.csv",",");
            assert productStrings!=null;
            auctionManager.populateProducts(productStrings);
            auctionManager.indexProducts();
            Product p1 = new Product.ProductBuilder("Spear","Marian").build(); //Fac un produs
            a1.addProduct(p1); // Il adaug la licitatie
            marian.addFounds(30000f);
            admin.addFounds(500f);
            var bidStrings = CSVReader.read("BidsCSV.csv",",");
            BidParser.populateBids(bidStrings);
            marian.indexBids(); //Acum nu mai are bids
            admin.placeBid(p1,300f);
            marian.placeBid(p1,100f); // Fac un  bid pentru produs
            p1.buyOut(); //Termin licitatia pentru produs
            System.out.println(p1.getOwner()); //Afisez noul owner
            marian.indexBids(); //Acum nu mai are bids
            Scanner scanner = new Scanner(System.in);

            String username;
            String password;
            boolean flag = true;
            User loggedUser = null;
            while (true) {
                int command = -1;
                welcome();
                while (command<0 || command >3) {
                    if(!flag)
                        System.out.println("Unknown command!");
                    flag = false;
                    System.out.print("Your choice: ");
                    command = scanner.nextInt();
                }

                while (loggedUser == null) {
                    switch (command) {
                        case 0: return;
                        case 1: //LogIn
                            System.out.print("Username: ");
                            username = scanner.next();
                            System.out.print("Password: ");
                            password = scanner.next();
                            loggedUser = userManager.logIn(username, password);
                            if (loggedUser == null)
                                System.out.println("There is no user with these credentials.");
                            break;
                        case 2:
                            loggedUser = readRegisterCredentials();
                            if (loggedUser == null)
                                System.out.println("There was a problem registering you account, please try again.");
                            break;
                    }
                }
                while (loggedUser != null)
                //Acum am un cont logged in
                {
                    System.out.println("Welcome, " + loggedUser.getName() + "!");
                    loggedUser.showPanel();
                    System.out.print("Your choice: ");
                    command = scanner.nextInt();

                    while (!loggedUser.checkCommand(Command.valueOf(command))) {
                        System.out.println("Unknown command!");
                        System.out.print("Your choice: ");
                        command = scanner.nextInt();
                    }
                    loggedUser = loggedUser.command(Command.valueOf(command));
                }
            } //Main while
        }
    }
