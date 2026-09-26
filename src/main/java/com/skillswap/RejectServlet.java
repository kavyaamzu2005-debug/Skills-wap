package com.skillswap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reject-request")
public class RejectServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/skillswap";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "SkillSwap@123";

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            response.getWriter().println("LOGIN_REQUIRED");

            return;
        }

        String email =
                (String) session.getAttribute("email");

        String connectionIdText =
                request.getParameter("connection_id");

        if (connectionIdText == null ||
                connectionIdText.trim().isEmpty()) {

            response.getWriter().println("INVALID_REQUEST");

            return;
        }

        int connectionId;

        try {

            connectionId =
                    Integer.parseInt(connectionIdText);

        } catch (NumberFormatException e) {

            response.getWriter().println("INVALID_REQUEST");

            return;
        }

        String checkSql =
                "SELECT c.id " +
                "FROM connections c " +
                "JOIN users u " +
                "ON u.id = c.receiver_id " +
                "WHERE c.id = ? " +
                "AND u.email = ? " +
                "AND c.status = 'pending'";

        String deleteSql =
                "DELETE FROM connections " +
                "WHERE id = ?";

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

            try (
                    Connection con =
                            DriverManager.getConnection(
                                    DB_URL,
                                    DB_USER,
                                    DB_PASSWORD
                            );

                    PreparedStatement check =
                            con.prepareStatement(checkSql);

                    PreparedStatement delete =
                            con.prepareStatement(deleteSql)
            ) {

                check.setInt(1, connectionId);

                check.setString(2, email);

                try (ResultSet rs =
                        check.executeQuery()) {

                    if (!rs.next()) {

                        response.getWriter().println(
                                "REQUEST_NOT_FOUND"
                        );

                        return;
                    }

                }

                delete.setInt(1, connectionId);

                int rowsDeleted =
                        delete.executeUpdate();

                if (rowsDeleted > 0) {

                    response.getWriter().println(
                            "REJECTED"
                    );

                } else {

                    response.getWriter().println(
                            "REQUEST_NOT_FOUND"
                    );

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "SERVER_ERROR"
            );

        }

    }

}
