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
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
 
public class CheckoutServlet extends HttpServlet {
 
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
      out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div id=\"nav\"> </div><body>");
 
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      String sqlStr = null;
      HttpSession session = null;
      Cart cart = null;
 
      try {
         conn = DriverManager.getConnection(databaseURL, username, password);  
         stmt = conn.createStatement();
         // Retrieve the Cart
         session = request.getSession(false);
         if (session == null) {
            out.println("<h3>Your Shopping cart is empty!</h3></body></html>");
            return;
         }
         synchronized (session) {
            cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
               out.println("<h3>Your Shopping cart is empty!</h3></body></html>");
               return;
            }
         }
 
         // Retrieve and process request parameters: id(s), cust_name, cust_email, cust_phone
         String custName = request.getParameter("cust_name");
         boolean hasCustName = custName != null && ((custName = custName.trim()).length() > 0);
         String custEmail = request.getParameter("cust_email").trim();
         boolean hasCustEmail = custEmail != null && ((custEmail = custEmail.trim()).length() > 0);
         String custPhone = request.getParameter("cust_phone").trim();
         boolean hasCustPhone = custPhone != null && ((custPhone = custPhone.trim()).length() > 0);
 
         // Validate inputs
         if (!hasCustName) {

            out.println("<h3>Please Enter Your Name!</h3></body></html>");
            return;
         }else if (!custName.matches("[a-zA-Z]*")){
             out.println("<h3>name must be characters only</h3></body></html>");
            return;
         }
         else if (!hasCustEmail || (custEmail.indexOf('@') == -1)) {
            out.println("<h3>Please Enter Your email (user@host)!</h3></body></html>");
            return;
         } else if (!hasCustPhone || custPhone.length() != 10) {
            out.println("<h3>Phone number must be 10-digits Phone Number!</h3></body></html>");
            return;
         }
 
         // Display the name, email and phone (arranged in a table)
         out.println("<div class=\"jumbotron text-center\">");
         out.println("<div class=\"container\">");
         out.println("<div class=\"row\">");
         out.println("<div class=\"col\">");
         
         
         out.println("<table class='table table-dark table-striped' border='1' cellpadding='6'>");
         out.println("<tr>");
         out.println("<td>Customer Name:</td>");
         out.println("<td>" + custName + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Customer Email:</td>");
         out.println("<td>" + custEmail + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Customer Phone Number:</td>");
         out.println("<td>" + custPhone + "</td></tr>");
         out.println("</table>");
         out.println("</div>");
 
         // Print the ordered products in a table
         out.println("<div class=\"col\">");
         out.println("<table class='table table-dark table-striped' border='1' cellpadding='6'>");
         out.println("<tr>");
         out.println("<th>Item name</th>");
         out.println("<th>Item Description</th>");
         out.println("<th>Price</th>");
         out.println("<th>Quantity</th></tr>");
         
         float totalPrice = 0f;
         for (CartItem item : cart.getItems()) {
            int id = item.getItemID();
               String itemName = item.getItemName();
               String itemDsc = item.getItemDsc();
               float price = item.getItemPrice();
               int qtyOrdered = item.getQtyOrdered();
            // Update products table and insert  order record
            sqlStr = "UPDATE PRODUCT SET QTY = QTY - " + qtyOrdered + " WHERE ITEMID = " + id;
            //System.out.println(sqlStr);  // for debugging
            stmt.executeUpdate(sqlStr);
            int orderId = 1;
            sqlStr = "SELECT COUNT(ID) FROM ORDER_RECORDS";
            //System.out.println(sqlStr);  // for debugging
         ResultSet size = stmt.executeQuery(sqlStr);
         if(size.next()){
             orderId = Integer.parseInt(size.getString(1));
             orderId++;
         }
            
            sqlStr = "INSERT INTO ORDER_RECORDS (ID, QTY_ORDERED, CUST_NAME, "
                    + "CUST_EMAIL, CUST_PHONE, ORDER_ID) VALUES("
                    + id + ", " + qtyOrdered + ", '" + custName + "', '"
                    + custEmail + "', '" + custPhone + "', " + orderId + ")";
            //System.out.println(sqlStr);  // for debugging
            
            stmt.executeUpdate(sqlStr);

 
            // Show the product ordered
            out.println("<tr>");
            out.println("<td>" + itemName + "</td>");
            out.println("<td>" + itemDsc + "</td>");
            out.println("<td>" + price + "</td>");
            out.println("<td>" + qtyOrdered + "</td></tr>");
            totalPrice += price * qtyOrdered;
         }
         out.println("<tr><td colspan='4' align='right'>Total Price: £");
         out.printf("%.2f</td></tr>", totalPrice);
         out.println("</table>");
         out.println("</div>");
         out.println("</div>");
         out.println("</div>");
 
         out.println("<h3>Thank you.</h3>");
         out.println("<a href='start'>Back to Search Menu</a>");
         out.println("</body></html>");
 
         cart.clear();   // empty the cart
      } catch (SQLException ex) {
         cart.clear();   // empty the cart
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(CheckoutServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();  // Return the connection to the pool
         } catch (SQLException ex) {
            Logger.getLogger(CheckoutServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}