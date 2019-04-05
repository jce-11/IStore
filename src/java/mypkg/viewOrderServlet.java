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
import static java.lang.System.console;
import java.sql.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class viewOrderServlet extends HttpServlet {
 
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
      Statement stmt = null;
 
      try {
         
         out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div  id=\"nav\"> </div><body>");
         
         out.println("<div class=\"jumbotron text-center\">");
            conn = DriverManager.getConnection(databaseURL, username, password);
            stmt = conn.createStatement();
            StringBuilder sqlStr = new StringBuilder();  
            sqlStr.append("SELECT * FROM PRODUCT P, order_records u WHERE p.itemid = u.id");
            ResultSet rset = stmt.executeQuery(sqlStr.toString());
            if (!rset.next()) {  // Check for empty ResultSet, no orders found
               out.println("<h3>No current orders!</h3>");
            } else {
               out.println("<h1> Orders</h1>");
               out.println("<table class='table table-dark table-striped' border='1' cellpadding='6'>");
               out.println("<tr>");
               out.println("<th>ID</th>");
               out.println("<th>Quantity ordered</th>");
               out.println("<th>Customer name</th>");
               out.println("<th>Customer email</th>");
               out.println("<th>Item ID</th>");
               out.println("<th>Item Name</th>");
               out.println("<th>Item Price</th>");
               out.println("</tr>");
               do {
                  out.println("<tr>");
                  out.println("<td>" + rset.getString("ORDER_ID") + "</td>");
                  out.println("<td>" + rset.getString("QTY_ORDERED") + "</td>");
                  out.println("<td>" + rset.getString("CUST_NAME") + "</td>");
                  out.println("<td>" + rset.getString("CUST_EMAIL") + "</td>");
                   out.println("<td>" + rset.getString("ITEMID") + "</td>");
                  out.println("<td>"+ rset.getString("ITEMNAME")+"</td>");
                  out.println("<td>"+ rset.getString("ITEMPRICE")+"</td>");
                  out.println("</tr>");
               } while (rset.next());
               out.println("</table><br />");
               out.println("<p><a href='start'>Back to Select Menu</a></p>");
               out.println("</div>");
            
         }
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}

