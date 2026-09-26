package com.skillswap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/skill")
public class SkillServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String skillName = request.getParameter("skill");
        String skillType = request.getParameter("type");

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email == null) {
            response.getWriter().println("<h2>Please login first!</h2>");
            return;
        }

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/skillswap",
                "root",
                "SkillSwap@123"
            );

            String findUser = "SELECT id FROM users WHERE email=?";

            PreparedStatement ps1 = con.prepareStatement(findUser);
            ps1.setString(1, email);

            var rs = ps1.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("id");

                String sql = "INSERT INTO skills (id, user_id, skill_name, skill_type) VALUES (NULL, ?, ?, ?)";

                PreparedStatement ps2 = con.prepareStatement(sql);

                ps2.setInt(1, userId);
                ps2.setString(2, skillName);
                ps2.setString(3, skillType);

                ps2.executeUpdate();

                ps2.close();
            }

            rs.close();
            ps1.close();
            con.close();

            response.sendRedirect("dashboard.html");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<h2>Skill could not be added!</h2>"
            );
        }
    }
}