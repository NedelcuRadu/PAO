package databaseConfig;
import models.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDatabaseManager {
public void insert(User user)
{
    String insertPersonSql = "INSERT INTO users VALUES(?, ?, ?, ?, ?)";
    Connection connection = DatabaseConfiguration.getDatabaseConnection();

    try {
        PreparedStatement preparedStatement = connection.prepareStatement(insertPersonSql);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setDate(2, (Date) user.getBirthDate());
        preparedStatement.setFloat(3, user.getFounds());
        preparedStatement.setString(4,user.getPasswordHash());
        preparedStatement.setDate(5, (Date) user.getRegisterDate());
        preparedStatement.execute();
    } catch (SQLException e)    {
        e.printStackTrace();
    }
}
}
