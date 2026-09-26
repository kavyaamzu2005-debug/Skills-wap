
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

@WebServlet("/recommended")
public class RecommendedServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/skillswap";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "SkillSwap@123";

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("email") == null) {

            response.getWriter()
                    .println("<p>Please login first.</p>");

            return;
        }

        String loggedInEmail =
                (String) session.getAttribute("email");

        PrintWriter out = response.getWriter();

        String sql =
                "SELECT u.id, u.name, u.email, " +
                "GROUP_CONCAT(CASE WHEN s.skill_type = 'teach' " +
                "THEN s.skill_name END SEPARATOR ', ') AS teach_skills, " +
                "GROUP_CONCAT(CASE WHEN s.skill_type = 'learn' " +
                "THEN s.skill_name END SEPARATOR ', ') AS learn_skills " +
                "FROM users u " +
                "LEFT JOIN skills s ON u.id = s.user_id " +
                "WHERE u.email <> ? " +

                "AND NOT EXISTS (" +
                "SELECT 1 FROM connections c " +
                "WHERE (c.sender_id = " +
                "(SELECT id FROM users WHERE email = ?) " +
                "AND c.receiver_id = u.id) " +
                "OR (c.receiver_id = " +
                "(SELECT id FROM users WHERE email = ?) " +
                "AND c.sender_id = u.id)" +
                ") " +

                "AND (" +

                "EXISTS (" +
                "SELECT 1 FROM skills mySkill " +
                "JOIN skills otherSkill " +
                "ON mySkill.skill_name = otherSkill.skill_name " +
                "WHERE mySkill.user_id = " +
                "(SELECT id FROM users WHERE email = ?) " +
                "AND mySkill.skill_type = 'learn' " +
                "AND otherSkill.user_id = u.id " +
                "AND otherSkill.skill_type = 'teach'" +
                ") " +

                "OR EXISTS (" +
                "SELECT 1 FROM skills mySkill " +
                "JOIN skills otherSkill " +
                "ON mySkill.skill_name = otherSkill.skill_name " +
                "WHERE mySkill.user_id = " +
                "(SELECT id FROM users WHERE email = ?) " +
                "AND mySkill.skill_type = 'teach' " +
                "AND otherSkill.user_id = u.id " +
                "AND otherSkill.skill_type = 'learn'" +
                ")" +

                ") " +

                "GROUP BY u.id, u.name, u.email " +
                "LIMIT 4";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con =
                    DriverManager.getConnection(
                            DB_URL,
                            DB_USER,
                            DB_PASSWORD);

                 PreparedStatement ps =
                    con.prepareStatement(sql)) {

                ps.setString(1, loggedInEmail);
                ps.setString(2, loggedInEmail);
                ps.setString(3, loggedInEmail);
                ps.setString(4, loggedInEmail);
                ps.setString(5, loggedInEmail);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        int userId = rs.getInt("id");

                        String name =
                                rs.getString("name");

                        String email =
                                rs.getString("email");

                        String teachSkills =
                                rs.getString("teach_skills");

                        String learnSkills =
                                rs.getString("learn_skills");

                        if (teachSkills == null) {
                            teachSkills = "Not added";
                        }

                        if (learnSkills == null) {
                            learnSkills = "Not added";
                        }

                        out.println(
                                "<div class='match-card'>" +

                                "<div class='profile-icon'>" +
                                "&#128100;</div>" +

                                "<h3>" + name + "</h3>" +

                                "<p><strong>Email:</strong> " +
                                email + "</p>" +

                                "<p><strong>" +
                                "Skills I Can Teach:" +
                                "</strong><br>" +
                                teachSkills + "</p>" +

                                "<p><strong>" +
                                "Skills I Want to Learn:" +
                                "</strong><br>" +
                                learnSkills + "</p>" +

                                "<form action='/webapp/connect' " +
                                "method='post'>" +

                                "<input type='hidden' " +
                                "name='receiver_id' " +
                                "value='" + userId + "'>" +

                                "<button type='submit'>" +
                                "Connect</button>" +

                                "</form>" +

                                "</div>"
                        );
                    }

                    if (!found) {

                        out.println(
                                "<p class='loading-text'>" +
                                "No new recommended matches found." +
                                "</p>"
                        );
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                    "<p class='loading-text'>" +
                    "Unable to load recommended matches." +
                    "</p>"
            );
        }
    }
}