
package com.skillswap;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/skillswap",
                "root",
                "SkillSwap@123"
            );

            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            ps.close();
            con.close();

            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");

            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Registration Successful | SkillSwap</title>");

            out.println("<style>");

            out.println("* {");
            out.println("    margin: 0;");
            out.println("    padding: 0;");
            out.println("    box-sizing: border-box;");
            out.println("}");

            out.println("body {");
            out.println("    font-family: 'Segoe UI', Arial, sans-serif;");
            out.println("    min-height: 100vh;");
            out.println("    background: linear-gradient(135deg, #f5f6ff, #eef0ff);");
            out.println("    color: #172554;");
            out.println("}");

            out.println(".navbar {");
            out.println("    height: 82px;");
            out.println("    background: #ffffff;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    justify-content: space-between;");
            out.println("    padding: 0 7%;");
            out.println("    box-shadow: 0 2px 15px rgba(79, 70, 229, 0.08);");
            out.println("}");

            out.println(".logo {");
            out.println("    font-size: 28px;");
            out.println("    font-weight: 700;");
            out.println("    color: #4f46e5;");
            out.println("}");

            out.println(".nav-links {");
            out.println("    display: flex;");
            out.println("    gap: 30px;");
            out.println("}");

            out.println(".nav-links a {");
            out.println("    text-decoration: none;");
            out.println("    color: #475569;");
            out.println("    font-size: 16px;");
            out.println("    font-weight: 500;");
            out.println("}");

            out.println(".nav-links a:hover {");
            out.println("    color: #4f46e5;");
            out.println("}");

            out.println(".page-container {");
            out.println("    min-height: calc(100vh - 82px);");
            out.println("    display: flex;");
            out.println("    justify-content: center;");
            out.println("    align-items: center;");
            out.println("    padding: 45px 20px;");
            out.println("}");

            out.println(".success-card {");
            out.println("    width: 100%;");
            out.println("    max-width: 650px;");
            out.println("    background: #ffffff;");
            out.println("    border: 1px solid #e0e7ff;");
            out.println("    border-radius: 24px;");
            out.println("    padding: 55px 45px;");
            out.println("    text-align: center;");
            out.println("    box-shadow: 0 20px 60px rgba(79, 70, 229, 0.12);");
            out.println("}");

            out.println(".success-icon {");
            out.println("    width: 95px;");
            out.println("    height: 95px;");
            out.println("    margin: 0 auto 28px;");
            out.println("    border-radius: 50%;");
            out.println("    background: #dcfce7;");
            out.println("    color: #16a34a;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    justify-content: center;");
            out.println("    font-size: 55px;");
            out.println("    font-weight: 700;");
            out.println("}");

            out.println("h1 {");
            out.println("    font-size: 34px;");
            out.println("    line-height: 1.3;");
            out.println("    margin-bottom: 18px;");
            out.println("    color: #172554;");
            out.println("}");

            out.println("h1 span {");
            out.println("    color: #4f46e5;");
            out.println("}");

            out.println(".message {");
            out.println("    color: #64748b;");
            out.println("    font-size: 17px;");
            out.println("    line-height: 1.7;");
            out.println("    margin-bottom: 32px;");
            out.println("}");

            out.println(".login-button {");
            out.println("    display: inline-block;");
            out.println("    padding: 15px 45px;");
            out.println("    background: linear-gradient(135deg, #4f46e5, #6366f1);");
            out.println("    color: #ffffff;");
            out.println("    text-decoration: none;");
            out.println("    border-radius: 12px;");
            out.println("    font-size: 17px;");
            out.println("    font-weight: 600;");
            out.println("    transition: 0.3s;");
            out.println("    box-shadow: 0 8px 20px rgba(79, 70, 229, 0.25);");
            out.println("}");

            out.println(".login-button:hover {");
            out.println("    transform: translateY(-2px);");
            out.println("    box-shadow: 0 12px 25px rgba(79, 70, 229, 0.35);");
            out.println("}");

            out.println(".home-link {");
            out.println("    display: inline-block;");
            out.println("    margin-top: 25px;");
            out.println("    color: #4f46e5;");
            out.println("    text-decoration: none;");
            out.println("    font-size: 15px;");
            out.println("    font-weight: 500;");
            out.println("}");

            out.println("@media (max-width: 600px) {");

            out.println("    .navbar {");
            out.println("        padding: 0 20px;");
            out.println("    }");

            out.println("    .logo {");
            out.println("        font-size: 23px;");
            out.println("    }");

            out.println("    .nav-links {");
            out.println("        gap: 12px;");
            out.println("    }");

            out.println("    .nav-links a {");
            out.println("        font-size: 13px;");
            out.println("    }");

            out.println("    .success-card {");
            out.println("        padding: 40px 22px;");
            out.println("    }");

            out.println("    h1 {");
            out.println("        font-size: 27px;");
            out.println("    }");

            out.println("    .message {");
            out.println("        font-size: 15px;");
            out.println("    }");

            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            out.println("<nav class='navbar'>");
            out.println("    <div class='logo'>SkillSwap</div>");

            out.println("    <div class='nav-links'>");
            out.println("        <a href='index.html'>Home</a>");
            out.println("        <a href='login.html'>Login</a>");
            out.println("        <a href='register.html'>Register</a>");
            out.println("    </div>");
            out.println("</nav>");

            out.println("<main class='page-container'>");

            out.println("    <div class='success-card'>");

            out.println("        <div class='success-icon'>✓</div>");

            out.println("        <h1>Registration <span>Successful!</span></h1>");

            out.println("        <p class='message'>");
            out.println("            Your account has been created successfully.<br>");
            out.println("            You can now log in and start your SkillSwap journey!");
            out.println("        </p>");

            out.println("        <a class='login-button' href='login.html'>");
            out.println("            Go to Login &nbsp; →");
            out.println("        </a>");

            out.println("        <br>");

            out.println("        <a class='home-link' href='index.html'>");
            out.println("            🏠 &nbsp; Back to Home");
            out.println("        </a>");

            out.println("    </div>");

            out.println("</main>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html;charset=UTF-8");

            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Registration Failed</title>");

            out.println("<style>");
            out.println("body {");
            out.println("    font-family: Arial, sans-serif;");
            out.println("    background: #fff1f2;");
            out.println("    text-align: center;");
            out.println("    padding: 100px 20px;");
            out.println("}");
            out.println("h2 { color: #dc2626; }");
            out.println("a { color: #4f46e5; text-decoration: none; }");
            out.println("</style>");

            out.println("</head>");
            out.println("<body>");

            out.println("<h2>Registration Failed!</h2>");
            out.println("<p>Please check your details and try again.</p>");
            out.println("<a href='register.html'>Back to Registration</a>");

            out.println("</body>");
            out.println("</html>");
        }
    }
}