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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email == null) {
            response.sendRedirect("login.html");
            return;
        }

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/skillswap",
                "root",
                "SkillSwap@123"
            );

            PreparedStatement ps = con.prepareStatement(
                "SELECT id, name, email FROM users WHERE email=?"
            );

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                response.getWriter().println("<h2>User not found!</h2>");
                return;
            }

            int userId = rs.getInt("id");
            String name = rs.getString("name");

            PreparedStatement skillPs = con.prepareStatement(
                "SELECT skill_name, skill_type FROM skills WHERE user_id=?"
            );

            skillPs.setInt(1, userId);

            ResultSet skills = skillPs.executeQuery();

            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>SkillSwap | Profile</title>");

            out.println("<style>");

            out.println("body{font-family:Arial;background:#f5f7fb;margin:0;padding:40px;}");

            out.println(".card{background:white;max-width:600px;margin:30px auto;padding:40px;border-radius:20px;box-shadow:0 5px 20px rgba(0,0,0,.1);}");

            out.println(".avatar{width:80px;height:80px;border-radius:50%;background:#4f46e5;color:white;display:flex;align-items:center;justify-content:center;margin:auto;font-size:30px;font-weight:bold;}");

            out.println("h1{text-align:center;color:#222;}");

            out.println(".email{text-align:center;color:#666;}");

            out.println("h3{margin-top:30px;color:#222;}");

            out.println(".skill{display:inline-block;background:#eeeeff;color:#4f46e5;padding:10px 18px;border-radius:20px;margin:5px;}");

            out.println(".back{display:block;text-align:center;margin-top:30px;color:#4f46e5;font-weight:bold;text-decoration:none;}");

            out.println("</style>");

            out.println("</head>");
            out.println("<body>");

            out.println("<div class='card'>");

            out.println("<div class='avatar'>" +
                        name.substring(0, 1).toUpperCase() +
                        "</div>");

            out.println("<h1>" + name + "</h1>");

            out.println("<p class='email'>" + email + "</p>");

            out.println("<hr>");

            out.println("<h3>Skills I Can Teach</h3>");

            boolean teachFound = false;

            while (skills.next()) {

                String skillName = skills.getString("skill_name");
                String skillType = skills.getString("skill_type");

                if ("teach".equalsIgnoreCase(skillType)) {

                    out.println("<span class='skill'>" +
                                skillName +
                                "</span>");

                    teachFound = true;
                }
            }

            if (!teachFound) {
                out.println("<p>No teaching skills added yet.</p>");
            }

            skills.close();
            skillPs.close();

            skillPs = con.prepareStatement(
                "SELECT skill_name, skill_type FROM skills WHERE user_id=?"
            );

            skillPs.setInt(1, userId);

            skills = skillPs.executeQuery();

            out.println("<h3>Skills I Want To Learn</h3>");

            boolean learnFound = false;

            while (skills.next()) {

                String skillName = skills.getString("skill_name");
                String skillType = skills.getString("skill_type");

                if ("learn".equalsIgnoreCase(skillType)) {

                    out.println("<span class='skill'>" +
                                skillName +
                                "</span>");

                    learnFound = true;
                }
            }

            if (!learnFound) {
                out.println("<p>No learning skills added yet.</p>");
            }

            out.println("<a class='back' href='dashboard.html'>Back to Dashboard</a>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

            skills.close();
            skillPs.close();
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<h2>Profile loading failed!</h2>"
            );
        }
    }
}