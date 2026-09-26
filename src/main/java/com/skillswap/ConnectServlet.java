
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

@WebServlet("/connect")
public class ConnectServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/skillswap";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "SkillSwap@123";

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().println("LOGIN_REQUIRED");
            return;
        }

        String email =
                (String) session.getAttribute("email");

        String receiverValue =
                request.getParameter("receiver_id");

        if (receiverValue == null ||
                receiverValue.isBlank()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().println("INVALID_USER");
            return;
        }

        int receiverId;

        try {
            receiverId = Integer.parseInt(receiverValue);

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().println("INVALID_USER");
            return;
        }

        String findSenderSql =
                "SELECT id FROM users WHERE email = ?";

        String checkSql =
                "SELECT id, status FROM connections " +
                "WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?) " +
                "LIMIT 1";

        String insertSql =
                "INSERT INTO connections " +
                "(sender_id, receiver_id, status) " +
                "VALUES (?, ?, 'pending')";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con =
                    DriverManager.getConnection(
                            DB_URL,
                            DB_USER,
                            DB_PASSWORD)) {

                int senderId;

                try (PreparedStatement ps =
                        con.prepareStatement(findSenderSql)) {

                    ps.setString(1, email);

                    try (ResultSet rs =
                            ps.executeQuery()) {

                        if (!rs.next()) {

                            response.setStatus(
                                    HttpServletResponse
                                            .SC_UNAUTHORIZED);

                            response.getWriter()
                                    .println("USER_NOT_FOUND");

                            return;
                        }

                        senderId = rs.getInt("id");
                    }
                }

                if (senderId == receiverId) {

                    response.getWriter()
                            .println("SELF_REQUEST");

                    return;
                }

                try (PreparedStatement ps =
                        con.prepareStatement(checkSql)) {

                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);
                    ps.setInt(3, receiverId);
                    ps.setInt(4, senderId);

                    try (ResultSet rs =
                            ps.executeQuery()) {

                        if (rs.next()) {

                            response.getWriter()
                                    .println("ALREADY_EXISTS");

                            return;
                        }
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(insertSql)) {

                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);

                    ps.executeUpdate();
                }

                response.getWriter()
                        .println("REQUEST_SENT");
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter()
                    .println("SERVER_ERROR");
        }
    }
}