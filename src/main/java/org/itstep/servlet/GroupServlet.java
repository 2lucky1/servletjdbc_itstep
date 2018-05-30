package org.itstep.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class GroupServlet extends HttpServlet {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/academy" +
            "?characterEncoding=UTF-8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "qwerty";

    private Connection connection;
    private String errorConnection;

    @Override
    public void init() throws ServletException {
        connectDb(); // подключение к б/д
    }

    private void connectDb() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            errorConnection = e.getLocalizedMessage();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");

        PrintWriter out = resp.getWriter();

        out.println("<!doctype html>" +
                "<html>" +
                "   <head>" +
                "       <title>Jdbc and Servlets lesson</title>" +
                "       <meta charset='utf-8'/>" +
                "       <link rel='stylesheet' href='/resources/css/style.css' />" +
                "   </head>" +
                "<body>");

        out.println("<h1>Group</h1>");

        if (connection == null) {
            connectDb();
        }

        if (connection == null) {
            // нет подключения к б/д
            out.println("<p style='color: red'>Error connection: " + errorConnection + "</p>");
        } else {
            // получаем данные с б/д
            try {
                printGroupsTable(out);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        out.print("</body>" +
                "</html>");
    }

    private void printGroupsTable(PrintWriter out) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT id, name FROM `group`";
            ResultSet resultSet = stmt.executeQuery(sql);
            out.println("<table>");
            out.println(" <thead>");
            out.println("     <tr>");
            out.println("         <th>Id</th>");
            out.println("         <th>Name</th>");
            out.println("     </tr>");
            out.println(" </thead>");
            out.println(" <tbody>");
            while (resultSet.next()) {
                out.println(" <tr>");
                out.format("     <td>%d</td>%n", resultSet.getInt("id"));
                out.format("     <td>%s</td>%n", resultSet.getString("name"));
                out.println(" </tr>");
            }
            out.println(" </tbody>");
            out.println("</table>");
        }
    }

    @Override
    public void destroy() {
        disconnectDb();
    }

    private void disconnectDb() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
