/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mypkg;

/**
 *
 * @author jasonevans
 */
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import javax.sql.*;
 
public class LoginServlet extends HttpServlet {
 
   private String databaseURL, username, password;
 
   @Override
   public void init(ServletConfig config) throws ServletException {
      super.init(config);
      ServletContext context = config.getServletContext();
      databaseURL = context.getInitParameter("databaseURL");
      username = context.getInitParameter("username");
      password = context.getInitParameter("password");
   }
 
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
 
      Connection conn = null;
      Statement  stmt = null;
      try {out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div  id=\"nav\"> </div><body>");
         
         out.println("<div class=\"jumbotron text-center\">");
 
         conn = DriverManager.getConnection(databaseURL, username, password);
            stmt = conn.createStatement();
 
         // Retrieve and process request parameters: username and password
         String userName = request.getParameter("username");
         String password = request.getParameter("password");
         String hashPass = null;
         boolean hasUserName = userName != null && ((userName = userName.trim()).length() > 0);
         boolean hasPassword = password != null && ((password = password.trim()).length() > 0);
         
         // Validate input request parameters
         if (!hasUserName) {
            out.println("<h3>Please Enter Your username!</h3>");
         } else if (!hasPassword) {
            out.println("<h3>Please Enter Your password!</h3>");
         } else {
             try{
              hashPass = AESCrypt.encrypt(password);//encrypt user password to see if matches saved
                }catch(Exception e) { System.out.println("bug"+e.getMessage()); }
            // Verify the username/password and retrieve the role(s)
            StringBuilder sqlStr = new StringBuilder();
            sqlStr.append("SELECT * FROM users WHERE ");
            sqlStr.append("USERNAME = '");
                  sqlStr.append(userName).append("'and PASSWORD = '");
                  sqlStr.append(hashPass).append("'");
            System.out.println(sqlStr);  
            ResultSet rset = stmt.executeQuery(sqlStr.toString());
 
            // Check if username/password are correct
            if (!rset.next()) {  // empty ResultSet
               out.println("<h3>Wrong username/password!</h3>");
               out.println("<p><a href='index.html'>Back to Login Menu</a></p>");
            } else {
                
               // Create a new HTTPSession and save the username and roles
               // First, invalidate the session. if any
               HttpSession session = request.getSession(false);
               if (session != null) {
                  session.invalidate();
               }
               session = request.getSession(true);
               synchronized (session) {
                  session.setAttribute("username", userName);
                  //session.setAttribute("roles", roles);
               }
 
               out.println("<p>Welcome, " + userName + "!</p>");
               out.println("<p><a href='add.jsp'>Add a product</a></p>");
               out.println("<p><a href='viewOrder'>View Customer Orders</a></p>");
            }
         }
         out.println("</body></html>");
 
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Try again later!</h3></body></html>");
         Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();  // Return the connection to the pool
         } catch (SQLException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}
