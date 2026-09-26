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

@WebServlet("/connections")
public class ConnectionsServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/skillswap";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "SkillSwap@123";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

      
        if ("true".equalsIgnoreCase(request.getParameter("stats"))) {

            sendStats(response);

            return;
        }

       
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);

        PrintWriter out = response.getWriter();

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
                "SELECT u.name, u.email, c.status, " +
                "CASE WHEN c.sender_id = me.id " +
                "THEN 'Sent' ELSE 'Received' END AS direction " +
                "FROM connections c " +
                "JOIN users me ON me.email = ? " +
                "JOIN users u ON u.id = " +
                "CASE WHEN c.sender_id = me.id " +
                "THEN c.receiver_id ELSE c.sender_id END " +
                "WHERE c.sender_id = me.id " +
                "OR c.receiver_id = me.id " +
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

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        String name =
                                rs.getString("name");

                        String otherEmail =
                                rs.getString("email");

                        String status =
                                rs.getString("status");

                        String direction =
                                rs.getString("direction");

                        String statusText;

                        if ("pending".equalsIgnoreCase(status)) {

                            statusText = "Pending";

                        } else if (
                                "connected".equalsIgnoreCase(status)
                        ) {

                            statusText = "Connected";

                        } else {

                            statusText = status;

                        }

                        out.println(

                                "<div class='connection-card'>" +

                                "<h3>" +
                                name +
                                "</h3>" +

                                "<p><strong>Email:</strong> " +
                                otherEmail +
                                "</p>" +

                                "<p><strong>Request:</strong> " +
                                direction +
                                "</p>" +

                                "<p><strong>Status:</strong> " +
                                statusText +
                                "</p>" +

                                "</div>"

                        );

                    }

                    if (!found) {

                        out.println(

                                "<p class='loading-text'>" +
                                "No connections yet. " +
                                "Find a match and connect!" +
                                "</p>"

                        );

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println(

                    "<p class='loading-text'>" +
                    "Unable to load connections." +
                    "</p>"

            );

        }

    }


  
    private void sendStats(HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String learnersSql =
                "SELECT COUNT(*) AS learners FROM users";

        String swapsSql =
                "SELECT COUNT(*) AS swaps " +
                "FROM connections " +
                "WHERE status = 'connected'";

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

            try (Connection con =
                         DriverManager.getConnection(
                                 DB_URL,
                                 DB_USER,
                                 DB_PASSWORD
                         );

                 PreparedStatement learnersPs =
                         con.prepareStatement(learnersSql);

                 PreparedStatement swapsPs =
                         con.prepareStatement(swapsSql);

                 ResultSet learnersRs =
                         learnersPs.executeQuery()) {

                int learners = 0;
                int swaps = 0;

                if (learnersRs.next()) {

                    learners =
                            learnersRs.getInt("learners");
                }

                try (ResultSet swapsRs =
                             swapsPs.executeQuery()) {

                    if (swapsRs.next()) {

                        swaps =
                                swapsRs.getInt("swaps");
                    }
                }

                out.println(
                        "{"
                        + "\"learners\":" + learners + ","
                        + "\"swaps\":" + swaps
                        + "}"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            out.println(
                    "{"
                    + "\"learners\":0,"
                    + "\"swaps\":0"
                    + "}"
            );
        }
    }
}