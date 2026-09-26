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

@WebServlet("/accept-request")
public class AcceptServlet extends HttpServlet {

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

        String findUserSql =
                "SELECT id FROM users WHERE email = ?";

        String updateSql =
                "UPDATE connections c " +
                "JOIN users u ON u.id = c.receiver_id " +
                "SET c.status = 'connected' " +
                "WHERE c.id = ? " +
                "AND u.email = ? " +
                "AND c.status = 'pending'";

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

                    PreparedStatement findUser =
                            con.prepareStatement(findUserSql);

                    PreparedStatement update =
                            con.prepareStatement(updateSql)
            ) {

                findUser.setString(1, email);

                try (ResultSet rs =
                        findUser.executeQuery()) {

                    if (!rs.next()) {

                        response.getWriter().println(
                                "USER_NOT_FOUND"
                        );

                        return;
                    }

                }

                update.setInt(1, connectionId);

                update.setString(2, email);

                int rowsUpdated =
                        update.executeUpdate();

                if (rowsUpdated > 0) {

                    response.getWriter().println(
                            "ACCEPTED"
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