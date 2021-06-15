package pl.coderslab.classes;

import pl.coderslab.utils.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class MainClass {

    public static void main(String[] args) {
        try (Connection conn = DbUtil.getConnection()) {

//            if (!DbUtil.createDB(conn)) {
//                System.out.println("Failed to create database");
//                System.exit(0);
//            }
            System.out.println("Connection to SQL");

//            workWithTheDatabase(conn, scann);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Failed connection to SQL");
        }
    }
//    /**
//     * @param scann Object Scanner
//     * @return Object user
//     */
//    public static User createUserInClass(Scanner scann) {
//        System.out.println("Creating a user...");
//        return new User(strEnteredFromTheConsole(scann, "Please enter username"),
//                strEnteredFromTheConsole(scann, "Please enter the user email"),
//                strEnteredFromTheConsole(scann, "Please enter the user password"));
//    }
//
//    /**
//     * The method receives text data from the console
//     *
//     * @param scanner         Object Scanner
//     * @param questionToEnter Text hint for data entry
//     * @return Data string entered
//     */
//    public static String strEnteredFromTheConsole(Scanner scanner, String questionToEnter) {
//        String str;
//        while (true) {
//            System.out.print(questionToEnter + ": ");
//            str = scanner.nextLine();
//            if ("".equals(str)) {
//                System.out.println("Data cannot be empty");
//                continue;
//            }
//            break;
//        }
//        return str;
//    }
//
//    public static int intEnteredFromTheConsole(Scanner scan, String questionToEnter) {
//        String n;
//
//        while (true) {
//            n = strEnteredFromTheConsole(scan, questionToEnter);
//            if (!isNumberGreaterEqualZero(n)) {
//                System.out.println("Incorrect argument");
//                continue;
//            }
//            break;
//        }
//        return Integer.parseInt(n);
//    }
//
//    public static boolean isNumberGreaterEqualZero(String input) {
//        if (NumberUtils.isParsable(input)) {
//            return Integer.parseInt(input) >= 0;
//        }
//        return false;
//    }
//    public static void workWithTheDatabase(Connection conn, Scanner scann) {
//
//        int index;
//        while (true) {
//            index = intEnteredFromTheConsole(scann, """
//                    Please enter the transaction number:
//                       1 - create user
//                       2 - search user by ID
//                       3 - update user by ID
//                       4 - delete user by ID
//                       5 - display a list of all users
//                       6 - search for a user by name
//                       0 - exit
//                    enter the number """);
//
//            switch (index) {
//                case 0 -> System.exit(0);
////                case 1 -> createUserInDb(conn, createUserInClass(scann));
////                case 2 -> DbUtil.getUser(conn,
////                        USER_SEARCH_BY_ID_QUERY,
////                        Integer.parseInt(strEnteredFromTheConsole(scann, "Please enter User ID"))).toString();
////                case 3 -> DbUtil.runUpdateQueryByID(conn,
////                        UPDATE_USER_QUERY,
////                        Integer.parseInt(strEnteredFromTheConsole(scann, "Please enter User ID")),
////                        "update",
////                        scann);
////                case 4 -> DbUtil.runUpdateQueryByID(conn,
////                        DELETE_USER_BY_ID_QUERY,
////                        Integer.parseInt(strEnteredFromTheConsole(scann, "Please enter User ID")),
////                        "delete",
////                        scann);
////                case 5 -> DbUtil.printAllUsers(Objects.requireNonNull(DbUtil.getAllUsers(conn, SELECT_ALL_USER_QUERY, scann, "all")));
////                case 6 -> DbUtil.printAllUsers(Objects.requireNonNull(DbUtil.getAllUsers(conn, SELECT_ALL_USER_WITH_NAME_QUERY, scann, "withName")));
//                default -> System.out.println("Please enter correct number");
//            }
//        }
//    }
}
