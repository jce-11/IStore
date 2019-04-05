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

 
public class CartServlet extends HttpServlet {
 
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
      // Retrieve current HTTPSession object. If none, create one.
      HttpSession session = request.getSession(true);
      Cart cart;
      synchronized (session) {  // synchronized to prevent concurrent updates
         // Retrieve the shopping cart for this session, if any. Otherwise, create one.
         cart = (Cart) session.getAttribute("cart");
         if (cart == null) {  // No cart, create one.
            cart = new Cart();
            session.setAttribute("cart", cart);  // Save it into session
         }
      }
      Connection conn   = null;
      Statement  stmt   = null;
      ResultSet  rset   = null;
      String     sqlStr = null;
      try {
         conn = DriverManager.getConnection(databaseURL, username, password);
            stmt = conn.createStatement();
 
         out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div  id=\"nav\"> </div><body>");
         
         out.println("<div class=\"jumbotron text-center\">");
         out.println("<h1>My Cart</h1>");
 
         // This servlet handles 4 cases:
         // (1) todo=add id=1001 qty1001=5 [id=1002 qty1002=1 ...]
         // (2) todo=update id=1001 qty1001=5
         // (3) todo=remove id=1001
         // (4) todo=view
         
         //Todo is a variable defined in the sent request which tells the servlet what to do
         String todo = request.getParameter("todo");
         if (todo == null) todo = "view";  // check is variable exists
         if (todo.equals("add") || todo.equals("update")) {
            String[] ids = request.getParameterValues("id");
            if (ids == null) {
               out.println("<h3>Please Select a product!</h3></body></html>");
               return;
            }
            // call database to get info on product from select id, loop through each retrived
            for (String id : ids) {
               sqlStr = "SELECT * FROM PRODUCT WHERE ITEMID = " + id;
               rset = stmt.executeQuery(sqlStr);
               rset.next(); 
               String category = rset.getString("CATEGORY");
               String itemName = rset.getString("ITEMNAME");
               float itemPrice = rset.getFloat("ITEMPRICE");
               int itemID = rset.getInt("ITEMID");
               String itemDsc = rset.getString("ITEMDSC");
 
               // Get quantity ordered - no error check!
               int qtyOrdered = Integer.parseInt(request.getParameter("qty" + id));
               int idInt = Integer.parseInt(id);
               if (todo.equals("add")) {
                  cart.add(category, itemID, itemName, itemDsc, itemPrice, qtyOrdered);
               } else if (todo.equals("update")) {
                  cart.update(idInt, qtyOrdered);
               }
            }
 
         } else if (todo.equals("remove")) {
             String id = request.getParameter("id");
             
            cart.remove(Integer.parseInt(id));
         }
 
         // All cases - Always display the shopping cart
         if (cart.isEmpty()) {
             out.println("<div class=\"jumbotron text-center\">");
            out.println("<p>Your shopping cart is empty</p>");
         } else {
             
             out.println("<div class=\"jumbotron text-center\">");
             out.println("<table class='table table-dark table-striped' border='1' cellpadding='6'>");
            out.println("<tr>");
            out.println("<th>AUTHOR</th>");
            out.println("<th>TITLE</th>");
            out.println("<th>PRICE</th>");
            out.println("<th>QTY</th>");
            out.println("<th>REMOVE</th></tr>");
 
            float totalPrice = 0f;
            for (CartItem item : cart.getItems()) {
               int id = item.getItemID();
               String itemName = item.getItemName();
               String itemDsc = item.getItemDsc();
               float price = item.getItemPrice();
               int qtyOrdered = item.getQtyOrdered();
 
               out.println("<tr>");
               out.println("<td>" + itemName + "</td>");
               out.println("<td>" + itemDsc +  "</td>");
               out.println("<td>" + price +  "</td>");
               out.println("<td><form method='get'>");
               out.println("<input type='hidden' name='todo' value='update' />");
               out.println("<input type='hidden' name='id' value='" + id + "' />");
               out.println("<input type='text' size='3' name='qty"
                       + id + "' value='" + qtyOrdered + "' />" );
               out.println("<input type='submit' value='Update' />");
               out.println("</form></td>");
               out.println("<td><form method='get'>");
               out.println("<input type='hidden' name='todo' value='remove' />");
               out.println("<input type='hidden' name='id' value='" + id + "' />");
               out.println("<input type='submit' value='remove' />");
               out.println("</form></td>");
 
               
               out.println("</tr>");
               totalPrice += price * qtyOrdered;
            }
            out.println("<tr><td colspan='5' align='right'>Total: £");
            out.printf("%.2f</td></tr>", totalPrice);
            out.println("</table>");
         }
 
 
         // Display the Checkout
         if (!cart.isEmpty()) {
            out.println("<br /><div class='container'><div class='row'>");
            out.println("<form method='get' action='checkout'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>Enter your Name:</td>");
            out.println("<td><input type='text' name='cust_name' /></td></tr>");
            out.println("<tr>");
            out.println("<td>Enter your Email:</td>");
            out.println("<td><input type='text' name='cust_email' /></td></tr>");
            out.println("<tr>");
            out.println("<td>Enter your Phone Number:</td>");
            out.println("<td><input type='text' name='cust_phone' /></td></tr>");
            out.println("<tr>");
            out.println("<td>Enter card details:</td>");
            out.println("<td><input type='text' /></td></tr>");
            
            out.println("<tr><td><input type='submit' value='CHECK OUT'></td></tr>");
            out.println("</table>");
            
            out.println("</form>");
            
            out.println("</div></div>");
         }
         out.println("</div>");
         out.println("</body></html>");
 
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();  // return the connection to the pool
         } catch (SQLException ex) {
            Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
         }

      }

   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}
