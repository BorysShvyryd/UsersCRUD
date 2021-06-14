package pl.coderslab.classes;

import org.apache.commons.lang3.ArrayUtils;
import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.classes.User;

import java.sql.*;
import java.util.Scanner;

public class DbUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/?useSSL=false&characterEncoding=utf8&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Borys19041986";

    private static final String NAME_DB = "workshop2";
    private static final String NAME_TABLE_DB = "users";

    private static final String CREATE_DB = """
            CREATE DATABASE %s
                CHARACTER SET utf8mb4
                COLLATE utf8mb4_unicode_ci""";
    private static final String CREATE_TABLE_USERS = """
            CREATE TABLE %s
            (   `id`       int(11)      NOT NULL AUTO_INCREMENT,
                `email`    varchar(255) NOT NULL UNIQUE COLLATE utf8mb4_unicode_ci,
                `username` varchar(255) NOT NULL COLLATE utf8mb4_unicode_ci,
                `password` varchar(255) NOT NULL COLLATE utf8mb4_unicode_ci,
                PRIMARY KEY (id)
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4
              COLLATE = utf8mb4_unicode_ci""";

    public static Connection getConnection() throws SQLException {
        System.out.println(UserDao.PURPLE + "Connecting to database..." + UserDao.RESET);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static boolean createDB(Connection conn, Scanner scanner) {
        boolean hasDB = false;
        try (Statement st = conn.createStatement()) {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getCatalogs();
            while (rs.next()) {
                String databaseName = rs.getString(1);
                if (databaseName.equalsIgnoreCase(NAME_DB)) {
                    hasDB = true;
                    break;
                }
            }
            if (!hasDB) {
                if ("Y".equalsIgnoreCase(UserDao.strEnteredFromTheConsole(scanner, UserDao.RED + "Database does not exist. Create a database? Y/N" + UserDao.RESET))) {
                    st.executeUpdate(String.format(CREATE_DB, NAME_DB));
                    System.out.println(UserDao.PURPLE + "Database created successfully..." + UserDao.RESET);
                    System.out.println(UserDao.PURPLE + "Creating a table..." + UserDao.RESET);
                    st.executeUpdate(String.format("USE %s", NAME_DB));
                    st.executeUpdate(String.format(CREATE_TABLE_USERS, NAME_TABLE_DB));
                    System.out.println(UserDao.PURPLE + "Table created successfully..." + UserDao.RESET);
                    hasDB = true;
                } else System.exit(0);
            }
            conn.setCatalog(NAME_DB);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return hasDB;
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static User getUser(Connection conn, String query, int userID) {
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            System.out.println(UserDao.PURPLE + "Search for a user by ID..." + UserDao.RESET);

            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
//            throw new DaoException("throw new ");
        }
        return null;
    }

    public static User[] getAllUsers(Connection conn, String query, Scanner scan, String arg) {

        if ("withName".equals(arg)) {
            String findString = "%" + UserDao.strEnteredFromTheConsole(scan, "Enter a search name: ") + "%";
            System.out.println(UserDao.PURPLE + "Search for all users where name \"..." + findString + "...\"" + UserDao.RESET);
            query = String.format(query, findString);
        }

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            System.out.println(UserDao.PURPLE + "Search for all users..." + UserDao.RESET);

            User[] users = new User[0];
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new User(resultSet.getInt("id"),
                        resultSet.getString("username"),
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

    public static void runUpdateQueryByID(Connection conn, String query, int id, String operation, Scanner scan) {
        try (PreparedStatement statement =
                     conn.prepareStatement(query)) {
            User user;
            if ("update".equalsIgnoreCase(operation)) {
                System.out.println(UserDao.PURPLE + "Update..." + UserDao.RESET);
                System.out.println("Enter data to change: (blank line - refuse to edit the field)");

                user = getUser(conn, UserDao.USER_SEARCH_BY_ID_QUERY, id);

                assert user != null;
                String st = UserDao.strEnteredFromTheConsole(scan, "Replace the name: " + user.getUserName() + " with : ");
                if (!"".equals(st)) user.setUserName(st);
                st = UserDao.strEnteredFromTheConsole(scan, "Replace the email: " + user.getEmail() + " with : ");
                if (!"".equals(st)) user.setEmail(st);
                st = UserDao.strEnteredFromTheConsole(scan, "Replace the password with : ");
                if (!"".equals(st)) user.setPassword(st);

                statement.setString(1, user.getUserName());
                statement.setString(2, user.getEmail());
                statement.setString(3, DbUtil.hashPassword(user.getPassword()));
                statement.setInt(4, id);
                System.out.println(UserDao.PURPLE + "Update complete." + UserDao.RESET);
            } else {
                System.out.println(UserDao.PURPLE + "Delete..." + UserDao.RESET);
                statement.setInt(1, id);
            }
            if (statement.executeUpdate() == 0)
                System.out.println(UserDao.RED + "No user with id number found" + UserDao.RESET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}