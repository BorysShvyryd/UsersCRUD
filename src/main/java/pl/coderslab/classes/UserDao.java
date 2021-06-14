package pl.coderslab.classes;

import org.apache.commons.lang3.math.NumberUtils;
import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.classes.DbUtil;
import pl.coderslab.classes.User;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class UserDao {
    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";
    public static final String PURPLE = "\033[0;35m";

    private static final String CREATE_USER_QUERY =" ";
//            """
//                    INSERT INTO users(username, email, password)
//                    VALUES (?, ?, ?)""";

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
                    FROM users""";
    private static final String SELECT_ALL_USER_WITH_NAME_QUERY =
            """
                    SELECT *
                    FROM users
                    WHERE username LIKE '%s'""";

    /**
     * @param conn Connection to database
     * @param user Object user
     */
    public static void createUserInDb(Connection conn, User user) {
        try (PreparedStatement statement = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());

            String myHashPass = DbUtil.hashPassword(user.getPassword());
            if (BCrypt.checkpw(user.getPassword(), myHashPass)) {
                statement.setString(3, myHashPass);
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    user.setId(resultSet.getInt(1));
                }
            } else
                System.out.println(RED + "Failed to hash password" + RESET);

        } catch (SQLException ex) {
            ex.printStackTrace();
//            throw new DaoException("Error creating database", ex);
        }
    }

    /**
     * @param scann Object Scanner
     * @return Object user
     */
    public static User createUserInClass(Scanner scann) {
        System.out.println("Creating a user...");
        return new User(strEnteredFromTheConsole(scann, "Please enter username"),
                strEnteredFromTheConsole(scann, "Please enter the user email"),
                strEnteredFromTheConsole(scann, "Please enter the user password"));
    }

    /**
     * The method receives text data from the console
     *
     * @param scanner         Object Scanner
     * @param questionToEnter Text hint for data entry
     * @return Data string entered
     */
    public static String strEnteredFromTheConsole(Scanner scanner, String questionToEnter) {
        String str;
        while (true) {
            System.out.print(questionToEnter + ": ");
            str = scanner.nextLine();
            if ("".equals(str)) {
                System.out.println(RED + "Data cannot be empty" + RESET);
                continue;
            }
            break;
        }
        return str;
    }

    public static int intEnteredFromTheConsole(Scanner scan, String questionToEnter) {
        String n;

        while (true) {
            n = strEnteredFromTheConsole(scan, questionToEnter);
            if (!isNumberGreaterEqualZero(n)) {
                System.out.println(RED + "Incorrect argument" + RESET);
                continue;
            }
            break;
        }
        return Integer.parseInt(n);
    }

    public static boolean isNumberGreaterEqualZero(String input) {
        if (NumberUtils.isParsable(input)) {
            return Integer.parseInt(input) >= 0;
        }
        return false;
    }

    public static void workWithTheDatabase(Connection conn, Scanner scann) {

        int index;
        while (true) {
            index = intEnteredFromTheConsole(scann, """
                    Please enter the transaction number:
                       1 - create user
                       2 - search user by ID
                       3 - update user by ID
                       4 - delete user by ID
                       5 - display a list of all users
                       6 - search for a user by name
                       0 - exit
                    enter the number """);

            switch (index) {
                case 0 -> System.exit(0);
                case 1 -> createUserInDb(conn, createUserInClass(scann));
                case 2 -> DbUtil.getUser(conn,
                        USER_SEARCH_BY_ID_QUERY,
                        Integer.parseInt(strEnteredFromTheConsole(scann, "Please enter User ID"))).toString();
                case 3 -> DbUtil.runUpdateQueryByID(conn,
                        UPDATE_USER_QUERY,
                        Integer.parseInt(strEnteredFromTheConsole(scann, "Please enter User ID")),
                        "update",
                        scann);
                case 4 -> DbUtil.runUpdateQueryByID(conn,
                        DELETE_USER_BY_ID_QUERY,
                        Integer.parseInt(strEnteredFromTheConsole(scann, "Please enter User ID")),
                        "delete",
                        scann);
                case 5 -> DbUtil.printAllUsers(Objects.requireNonNull(DbUtil.getAllUsers(conn, SELECT_ALL_USER_QUERY, scann, "all")));
                case 6 -> DbUtil.printAllUsers(Objects.requireNonNull(DbUtil.getAllUsers(conn, SELECT_ALL_USER_WITH_NAME_QUERY, scann, "withName")));
                default -> System.out.println(RED + "Please enter correct number" + RESET);
            }
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DbUtil.getConnection()) {
            Scanner scann = new Scanner(System.in);

            if (!DbUtil.createDB(conn, scann)) {
                System.out.println("Failed to create database");
                System.exit(0);
            }

            workWithTheDatabase(conn, scann);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Failed connection to SQL");
        }
    }
}
