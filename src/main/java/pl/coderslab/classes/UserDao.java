package pl.coderslab.classes;

import org.apache.commons.lang3.ArrayUtils;
import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.DbUtil;

import java.sql.*;
import java.util.Scanner;

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
                    DELETE\s
                    FROM users
                    WHERE id = ?""";
    private static final String SELECT_ALL_USER_QUERY =
            """
                    SELECT *
                    FROM user""";
    private static final String SELECT_ALL_USER_WITH_NAME_QUERY =
            """
                    SELECT *
                    FROM users
                    WHERE username LIKE '%s'""";

    /**
     * @param user Object user
     */
    public static void createUserInDb(User user) {
//        try (PreparedStatement statement = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS)) {
//            statement.setString(1, user.getUserName());
//            statement.setString(2, user.getEmail());

            String myHashPass = hashPassword(user.getPassword());
            if (BCrypt.checkpw(user.getPassword(), myHashPass)) {
//                statement.setString(3, myHashPass);
//                statement.executeUpdate();
//                ResultSet resultSet = statement.getGeneratedKeys();
//                if (resultSet.next()) {
//                    user.setId(resultSet.getInt(1));
//                }
                System.out.println(RED + "UserDao: line 62, ...." + RESET);
            } else
                System.out.println(RED + "Failed to hash password. UserDao: line 64" + RESET);

//        } catch (SQLException ex) {
//            ex.printStackTrace();
////            throw new DaoException("Error creating database", ex);
//        }
    }
    public static User[] getAllUsers(Connection conn, String query, Scanner scan, String arg) {

//        if ("withName".equals(arg)) {
//            String findString = "%" + UserDao.strEnteredFromTheConsole(scan, "Enter a search name: ") + "%";
//            System.out.println(UserDao.PURPLE + "Search for all users where name \"..." + findString + "...\"" + UserDao.RESET);
//            query = String.format(query, findString);
//        }

        try (PreparedStatement statement = conn.prepareStatement(query)) {
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

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static User getUser(int userID) {
//        try (PreparedStatement statement = conn.prepareStatement(query)) {
            System.out.println(UserDao.PURPLE + "Search for a user by ID... UserDao.getUser" + UserDao.RESET);

//            statement.setInt(1, userID);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return new User(resultSet.getString("username"),
//                        resultSet.getString("email"),
//                        resultSet.getString("password"));
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
////            throw new DaoException("throw new ");
//        }
        return null;
    }

    public static void deleteUserById(int id) {

    }

    public static User[] getAllUsers() {//
        // Connection conn, String query, Scanner scan, String arg) {

//        if ("withName".equals(arg)) {
////            String findString = "%" + UserDao.strEnteredFromTheConsole(scan, "Enter a search name: ") + "%";
//            String findString = "%" + "Enter a search name: " + "%";
//            System.out.println(UserDao.PURPLE + "Search for all users where name \"..." + findString + "...\"" + UserDao.RESET);
//            query = String.format(query, findString);
//        }

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

//    public static void printUser(User user) {
//        if (user != null) {
//            System.out.format("%6s | %20s | %25s | %20s",
//                    user.getId(),
//                    user.getUserName(),
//                    user.getEmail(),
//                    user.getPassword());
//            System.out.println();
//        } else
//            System.out.println(UserDao.RED + "No user with id number found" + UserDao.RESET);
//    }

    public static void printAllUsers(User[] users) {
        if (users.length > 0)
            for (User u : users) {
                u.toString();
            }
        else System.out.println(UserDao.RED + "No users" + UserDao.RESET);
    }

//    public static void runUpdateQueryByID(Connection conn, String query, int id, String operation, Scanner scan) {
//        try (PreparedStatement statement =
//                     conn.prepareStatement(query)) {
//            User user;
//            if ("update".equalsIgnoreCase(operation)) {
//                System.out.println(UserDao.PURPLE + "Update..." + UserDao.RESET);
//                System.out.println("Enter data to change: (blank line - refuse to edit the field)");
//
//                user = getUser(conn, UserDao.USER_SEARCH_BY_ID_QUERY, id);
//
//                assert user != null;
//                String st = UserDao.strEnteredFromTheConsole(scan, "Replace the name: " + user.getUserName() + " with : ");
//                if (!"".equals(st)) user.setUserName(st);
//                st = UserDao.strEnteredFromTheConsole(scan, "Replace the email: " + user.getEmail() + " with : ");
//                if (!"".equals(st)) user.setEmail(st);
//                st = UserDao.strEnteredFromTheConsole(scan, "Replace the password with : ");
//                if (!"".equals(st)) user.setPassword(st);
//
//                statement.setString(1, user.getUserName());
//                statement.setString(2, user.getEmail());
//                statement.setString(3, pl.coderslab.utils.DbUtil.hashPassword(user.getPassword()));
//                statement.setInt(4, id);
//                System.out.println(UserDao.PURPLE + "Update complete." + UserDao.RESET);
//            } else {
//                System.out.println(UserDao.PURPLE + "Delete..." + UserDao.RESET);
//                statement.setInt(1, id);
//            }
//            if (statement.executeUpdate() == 0)
//                System.out.println(UserDao.RED + "No user with id number found" + UserDao.RESET);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
