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
import java.sql.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class OrderServlet extends HttpServlet {
 
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
      ResultSet  rset = null;
      String     sqlStr = null;
 
      try {
         out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div class=\"container-fluid\" id=\"nav\"> </div><body>");
 
         // Retrieve and process request parameters: id(s), cust_name, cust_email, cust_phone
         String[] ids = request.getParameterValues("id");  // Possibly more than one values
         String custName = request.getParameter("cust_name");
         boolean hasCustName = custName != null && ((custName = custName.trim()).length() > 0);
         String custEmail = request.getParameter("cust_email").trim();
         boolean hasCustEmail = custEmail != null && ((custEmail = custEmail.trim()).length() > 0);
         String custPhone = request.getParameter("cust_phone").trim();
         boolean hasCustPhone = custPhone != null && ((custPhone = custPhone.trim()).length() > 0);
 
         // Validate inputs
         if (ids == null || ids.length == 0) {
            out.println("<h3>Please Select a Book!</h3>");
         } else if (!hasCustName) {
            out.println("<h3>Please Enter Your Name!</h3>");
         } else if (!hasCustEmail || (custEmail.indexOf('@') == -1)) {
            out.println("<h3>Please Enter Your e-mail (user@host)!</h3>");
         } else if (!hasCustPhone || (custPhone.length() != 8)) {
            out.println("<h3>Please Enter an 8-digit Phone Number!</h3>");
         } else {
            // Display the name, email and phone (arranged in a table)
            out.println("<table>");
            out.println("<tr><td>Customer Name:</td><td>" + custName + "</td></tr>");
            out.println("<tr><td>Customer Email:</td><td>" + custEmail + "</td></tr>");
            out.println("<tr><td>Customer Phone Number:</td><td>" + custPhone + "</td></tr></table>");
 
            conn = DriverManager.getConnection(databaseURL, username, password);
            stmt = conn.createStatement();
 
            // Print the book(s) ordered in a table
            out.println("<br />");
            out.println("<table border='1' cellpadding='6'>");
            out.println("<tr><th>AUTHOR</th><th>TITLE</th><th>PRICE</th><th>QTY</th></tr>");
 
            float totalPrice = 0f;
            for (String id : ids) {
               sqlStr = "SELECT * FROM PRODUCT WHERE ITEMID = " + id;
               //System.out.println(sqlStr);  // for debugging
               rset = stmt.executeQuery(sqlStr);
 
               // Expect only one row in ResultSet
               rset.next();
               String category = rset.getString("CATEGORY");
               String itemName = rset.getString("ITEMNAME");
               String itemDsc = rset.getString("ITEMDSC");
               float price = rset.getFloat("ITEMPRICE");
 
               int qtyOrdered = Integer.parseInt(request.getParameter("qty" + id));
               
 
               sqlStr = "INSERT INTO order_records values ("
                       + id + ", " + qtyOrdered + ", '" + custName + "', '"
                       + custEmail + "', '" + custPhone + "')";
               //System.out.println(sqlStr);  // for debugging
               stmt.executeUpdate(sqlStr);
 
               // Display this book ordered
               out.println("<tr>");
               out.println("<td>" + category + "</td>");
               out.println("<td>" + itemName + "</td>");
               out.println("<td>" + itemDsc + "</td>");
               out.println("<td>" + price + "</td>");
               out.println("<td>" + qtyOrdered + "</td></tr>");
               totalPrice += price * qtyOrdered;
            }
 
            out.println("<tr><td colspan='4' align='right'>Total Price: £");
            out.printf("%.2f</td></tr>", totalPrice);
            out.println("</table>");
 
            out.println("<h3>Thank you.</h3>");
            out.println("<p><a href='start'>Back to Select Menu</a></p>");
            
         }
         out.println("</body></html>");
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}
