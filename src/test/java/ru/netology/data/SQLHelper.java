package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLHelper {
    private static QueryRunner runner = new QueryRunner();
    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");

    private SQLHelper() {
    }

    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection(url, user, password);
    }

    @SneakyThrows
    public static void cleanDB() {
        var connection = getConn();
        runner.execute(connection, "DELETE FROM order_entity");
        runner.execute(connection, "DELETE FROM payment_entity");
        runner.execute(connection, "DELETE FROM credit_request_entity");
    }

    @SneakyThrows
    public static String getStatusOfCardAfterPayment() {
        var SQLQuery = "SELECT status FROM payment_entity WHERE transaction_id IN (SELECT payment_id FROM order_entity) ORDER BY created DESC LIMIT 1";
        var connection = getConn();
        return runner.query(connection, SQLQuery, new ScalarHandler<>());
    }

    @SneakyThrows
    public static String getStatusOfCardAfterCredit() {
        var SQLQuery = "SELECT status FROM credit_request_entity WHERE bank_id IN (SELECT payment_id FROM order_entity) ORDER BY created DESC LIMIT 1";
        var connection = getConn();
        return runner.query(connection, SQLQuery, new ScalarHandler<>());
    }

    @SneakyThrows
    public static Integer getAmountOfPayment() {
        var SQLQuery = "SELECT amount FROM payment_entity WHERE transaction_id IN (SELECT payment_id FROM order_entity) ORDER BY created DESC LIMIT 1";
        var connection = getConn();
        return runner.query(connection, SQLQuery, new ScalarHandler<>());
    }
}
