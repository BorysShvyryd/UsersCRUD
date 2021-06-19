package pl.coderslab.controler;

import org.apache.commons.lang3.ArrayUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDao {
    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";
    public static final String PURPLE = "\033[0;35m";

    private static final String CREATE_DB = """
            CREATE DATABASE users
                CHARACTER SET utf8mb4
                COLLATE utf8mb4_unicode_ci""";
    private static final String CREATE_TABLE_USERS = """
            CREATE TABLE user
            (   `id`       int(11)      NOT NULL AUTO_INCREMENT,
                `email`    varchar(255) NOT NULL UNIQUE COLLATE utf8mb4_unicode_ci,
                `username` varchar(255) NOT NULL COLLATE utf8mb4_unicode_ci,
                `password` varchar(255) NOT NULL COLLATE utf8mb4_unicode_ci,
                PRIMARY KEY (id)
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4
              COLLATE = utf8mb4_unicode_ci""";

    private static final String CREATE_USER_QUERY =
            """
                    INSERT INTO users(username, email, password)
                    VALUES (?, ?, ?)""";

    public static final String USER_SEARCH_BY_ID_QUERY =
            """
                    SELECT *
                    FROM users
                    WHERE id = ?""";
    private static final String UPDATE_USER_QUERY =
            """
                    UPDATE users SET username = ?,
                    email = ?,
                    password = ? WHERE id = ?""";
    private static final String DELETE_USER_BY_ID_QUERY =
            """
                    DELETE
                    FROM users
                    WHERE id = ?""";
    private static final String SELECT_ALL_USER_QUERY =
            """
                    SELECT *
                    FROM users""";
    private static final String SELECT_ALL_USER_WITH_NAME_QUERY =
            """
                    SELECT *
                    FROM users
                    WHERE username LIKE '%s'""";

    /**
     * @param user Object user
     */
    public static void createUserInDb(User user) {
        try (PreparedStatement ps = DbUtil.getConnection().prepareStatement(CREATE_USER_QUERY)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            String myHashPass = hashPassword(user.getPassword());
            if (BCrypt.checkpw(user.getPassword(), myHashPass)) {
                ps.setString(3, hashPassword(user.getPassword()));
                ps.executeUpdate();
                System.out.println(UserDao.PURPLE + "Hash password status ok..." + UserDao.RESET);
            } else
                System.out.println(RED + "Failed to hash password." + RESET);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static User getUser(int userID) {
        System.out.println(UserDao.PURPLE + "Search for a user by ID... UserDao.getUser" + UserDao.RESET);

        try (PreparedStatement ps = DbUtil.getConnection().prepareStatement(USER_SEARCH_BY_ID_QUERY)) {
            ps.setInt(1, userID);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                System.out.println("nema Userdao97");
                return new User(resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void deleteUserById(int id) {
        try (PreparedStatement statement = DbUtil.getConnection().prepareStatement(DELETE_USER_BY_ID_QUERY)) {
            System.out.println(UserDao.PURPLE + "Delete user by id = " + id + "..." + UserDao.RESET);

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void updateUser(User user) {
        try (PreparedStatement statement = DbUtil.getConnection().prepareStatement(UPDATE_USER_QUERY)) {
            System.out.println(UserDao.PURPLE + "Update user by id = " + user.getId() + "..." + UserDao.RESET);

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static User[] getAllUsers() {

        try (PreparedStatement statement = DbUtil.getConnection().prepareStatement(SELECT_ALL_USER_QUERY)) {
            System.out.println(UserDao.PURPLE + "Search for all users..." + UserDao.RESET);

            User[] users = new User[0];
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new User(resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
                users = ArrayUtils.add(users, user);
            }
            return users;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
