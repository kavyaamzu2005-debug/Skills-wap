
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

@WebServlet("/match")
public class MatchServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/skillswap";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD =
            "SkillSwap@123";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            response.getWriter().println(
                    "<p>Please login first.</p>"
            );

            return;
        }

        String loggedInEmail =
                (String) session.getAttribute("email");

        PrintWriter out = response.getWriter();

        String sql =
                "SELECT u.id, u.name, u.email, " +

                "GROUP_CONCAT(DISTINCT " +
                "CASE WHEN s.skill_type = 'teach' " +
                "THEN s.skill_name END SEPARATOR ', ') " +
                "AS teach_skills, " +

                "GROUP_CONCAT(DISTINCT " +
                "CASE WHEN s.skill_type = 'learn' " +
                "THEN s.skill_name END SEPARATOR ', ') " +
                "AS learn_skills, " +

                "MAX(c.status) AS connection_status " +

                "FROM users u " +

                "JOIN users me ON me.email = ? " +

                "LEFT JOIN skills s " +
                "ON u.id = s.user_id " +

                "LEFT JOIN connections c " +
                "ON ( " +
                "(c.sender_id = me.id AND c.receiver_id = u.id) " +
                "OR " +
                "(c.sender_id = u.id AND c.receiver_id = me.id) " +
                ") " +

                "WHERE u.email <> ? " +

                "GROUP BY u.id, u.name, u.email " +

                "ORDER BY u.id";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    DB_URL,
                    DB_USER,
                    DB_PASSWORD
            );

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, loggedInEmail);
            ps.setString(2, loggedInEmail);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                int userId = rs.getInt("id");

                String name = rs.getString("name");

                String email = rs.getString("email");

                String teachSkills =
                        rs.getString("teach_skills");

                String learnSkills =
                        rs.getString("learn_skills");

                String connectionStatus =
                        rs.getString("connection_status");

                if (teachSkills == null ||
                        teachSkills.trim().isEmpty()) {

                    teachSkills = "Not added yet";
                }

                if (learnSkills == null ||
                        learnSkills.trim().isEmpty()) {

                    learnSkills = "Not added yet";
                }

                if (connectionStatus == null) {

                    connectionStatus = "none";
                }

                out.println(

                        "<div class='match-card'>"

                        + "<div class='profile-icon'>"
                        + "&#128100;"
                        + "</div>"

                        + "<h3>" + name + "</h3>"

                        + "<p><strong>Email:</strong> "
                        + email
                        + "</p>"

                        + "<p><strong>Skills I Can Teach:</strong><br>"
                        + teachSkills
                        + "</p>"

                        + "<p><strong>Skills I Want to Learn:</strong><br>"
                        + learnSkills
                        + "</p>"
                );

                if ("connected".equalsIgnoreCase(connectionStatus)) {

                    out.println(
                            "<button type='button' "
                            + "disabled "
                            + "style='background:#16a34a; "
                            + "color:white; "
                            + "cursor:not-allowed;'>"
                            + "&#10003; Connected"
                            + "</button>"
                    );

                } else if ("pending".equalsIgnoreCase(connectionStatus)) {

                    out.println(
                            "<button type='button' "
                            + "disabled "
                            + "style='background:#f59e0b; "
                            + "color:white; "
                            + "cursor:not-allowed;'>"
                            + "&#9203; Pending"
                            + "</button>"
                    );

                } else {

                    out.println(

                            "<form action='/webapp/connect' "
                            + "method='post'>"

                            + "<input type='hidden' "
                            + "name='receiver_id' "
                            + "value='" + userId + "'>"

                            + "<button type='submit'>"
                            + "Connect"
                            + "</button>"

                            + "</form>"
                    );
                }

                out.println("</div>");
            }

            if (!found) {

                out.println(
                        "<p>No users available.</p>"
                );
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                    "<p>Unable to load users and skills.</p>"
            );
        }
    }
}