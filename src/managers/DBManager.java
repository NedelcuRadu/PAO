package managers;

import IOClasses.WriteToFile;
import databaseConfig.DatabaseConfiguration;
import models.Model;
import models.User;
import org.jetbrains.annotations.NotNull;
import validators.DataValidator;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DBManager {
    public static <T extends Model> T insert(T obj) {
        {
            String insertSql = obj.getInsertStatement();
            Connection connection = DatabaseConfiguration.getDatabaseConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                int rowCount = preparedStatement.executeUpdate();
                if (rowCount == 0) {
                    System.out.println("Failed the following statement: " + insertSql);
                    return null;
                }
                    return obj;

            }
            catch (SQLIntegrityConstraintViolationException e)
            {
                System.out.println("There is already an entry with the primary key "+obj.getPK());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T extends Model> void delete(@NotNull T obj) {
        String deleteSql = "DELETE FROM " + obj.getTableName() + " WHERE ID = " + obj.getPK();
        Connection connection = DatabaseConfiguration.getDatabaseConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static <T extends Model> void delete(String id, String tableName) {
        String deleteSql = "DELETE FROM " + tableName + " WHERE ID = "+ DataValidator.escapeString(id);
        System.out.println("Executing: "+deleteSql);
        Connection connection = DatabaseConfiguration.getDatabaseConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Model> T update(@NotNull T newObj) {
        Map<String, String> props = newObj.getValues();
        String baseQuery = "UPDATE " + newObj.getTableName() +" SET ";
        StringBuilder updateSql = new StringBuilder(baseQuery);
        for (var prop : props.keySet()) {
            updateSql.append(prop).append(" = ").append(props.get(prop)).append(", ");
        }
        updateSql.delete(updateSql.length() - 2, updateSql.length());
        updateSql.append(" WHERE ID = ").append(newObj.getPK());
        Connection connection = DatabaseConfiguration.getDatabaseConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateSql.toString());
            System.out.println("Executing "+ updateSql.toString());
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount > 0) {
                return newObj;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T extends Model> T createOrFind(T obj)
    {
        var tmp = insert(obj); // Try to create the object
        if (tmp == null) // If it's null, it means it already exists
            findById(obj,obj.getPK());
        return obj;
    }
    public static <T extends Model> Optional<T> findById(T obj, String id) {
        String selectSql = "SELECT * FROM "+obj.getTableName()+" WHERE ID = ?";
        Connection connection = DatabaseConfiguration.getDatabaseConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                obj.setInfo(rowToMap(resultSet));
            return Optional.of(obj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static List<Map<String, String>> index(String tableName) {
        String selectSql = "SELECT * FROM " + tableName;
        Connection connection = DatabaseConfiguration.getDatabaseConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Map<String, String>> objs = new ArrayList<>();
                while (resultSet.next()) {
                    objs.add(rowToMap(resultSet));
                }
                return objs;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    private static Map<String, String> rowToMap(ResultSet row) {

        try {

            ResultSetMetaData resultSetMetaData = row.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            Map<String, String> obj = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i).toUpperCase(Locale.ROOT);
                String columnValue = row.getString(i);
                obj.put(columnName, columnValue);
            }
            return obj;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        WriteToFile.writeLn("logging.txt","nume_actiune,timestamp");

        //region Index
        var results =  DBManager.index("USERS");
        assert results != null;
        //endregion
        //region Insert
        var userManager = UserManager.getInstance();
        userManager.parseList(results);
        userManager.index();
        var marian = userManager.createUser("Marian Andrei Honcea7", new Date(), "ana");
        var marianID = "Marian Andrei Honcea7";
        //region Index After Insert
        var resultsAfterInsert =  DBManager.index("USERS");
        assert resultsAfterInsert != null;
        for (var result:resultsAfterInsert)
            System.out.println(result);
        //endregion
        //endregion
        //region Update
        System.out.println("Adding Founds");
        marian.addFounds(340f);
        // endregion
        //region Delete
        userManager.delete(marian);
        //endregion
        //region Find
        Optional<User> found = DBManager.findById(new User(),marianID); //Marian e sters, nu il mai gaseste
        System.out.println(found);
        //endregion
    }


    public static void mainAuctions(String[] args) {
        WriteToFile.writeLn("logging.txt","nume_actiune,timestamp");
        var auctionManager = AuctionManager.getInstance();
        var userManager = UserManager.getInstance();
        //region Index
        var results = DBManager.index("USERS");
        userManager.parseList(results);
        //userManager.index(); #Acum am userii in cache
        results =  DBManager.index("AUCTIONS");
        assert results != null;
        for (var result:results)
            System.out.println(result);
        auctionManager.parseList(results);
        auctionManager.index();

        //endregion
        //region Insert
        var newAuction = AuctionManager.getInstance().createAuction("Dorin Hana","Insert Test v14",DataValidator.convertToValidDate("2021-12-20"));
        auctionManager.index();
        auctionManager.delete(newAuction);

        //endregion

        //region Update
        newAuction.setName("Insert Test Updated v1");
        auctionManager.index();
        //endregion
    }
}
