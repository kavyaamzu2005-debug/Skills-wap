package com.skillswap;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/connection-requests")
public class ConnectionRequestsServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/skillswap";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "SkillSwap@123";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            out.println(
                    "<p class='loading-text'>" +
                    "Please login first." +
                    "</p>"
            );

            return;
        }

        String email =
                (String) session.getAttribute("email");

        String sql =
                "SELECT c.id, u.name, u.email " +
                "FROM connections c " +
                "JOIN users receiver " +
                "ON receiver.id = c.receiver_id " +
                "JOIN users u " +
                "ON u.id = c.sender_id " +
                "WHERE receiver.email = ? " +
                "AND c.status = 'pending' " +
                "ORDER BY c.id DESC";

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

                    PreparedStatement ps =
                            con.prepareStatement(sql)
            ) {

                ps.setString(1, email);

                try (ResultSet rs =
                        ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        int connectionId =
                                rs.getInt("id");

                        String senderName =
                                rs.getString("name");

                        String senderEmail =
                                rs.getString("email");

                        out.println(

                                "<div class='connection-card'>" +

                                "<h3>" +
                                senderName +
                                "</h3>" +

                                "<p><strong>Email:</strong> " +
                                senderEmail +
                                "</p>" +

                                "<p><strong>Status:</strong> " +
                                "Pending" +
                                "</p>" +

                                "<button " +
                                "type='button' " +
                                "onclick='acceptRequest(" +
                                connectionId +
                                ")'>" +
                                "Accept" +
                                "</button>" +

                                "<button " +
                                "type='button' " +
                                "onclick='rejectRequest(" +
                                connectionId +
                                ")'>" +
                                "Reject" +
                                "</button>" +

                                "</div>"

                        );

                    }

                    if (!found) {

                        out.println(

                                "<p class='loading-text'>" +
                                "No pending connection requests." +
                                "</p>"

                        );

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println(

                    "<p class='loading-text'>" +
                    "Unable to load connection requests." +
                    "</p>"

            );

        }

    }

}