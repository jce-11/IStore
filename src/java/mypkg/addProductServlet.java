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
 
public class addProductServlet extends HttpServlet {
 
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
         // Retrieve and process request product parameters: 
         String category = request.getParameter("category");
         boolean hasCategory = category != null && ((category = category.trim()).length() > 0);
         String itemName = request.getParameter("itemName").trim();
         boolean hasName = itemName != null && ((itemName = itemName.trim()).length() > 0);
         String itemDsc = request.getParameter("itemDsc").trim();
         boolean hasItemDsc = itemDsc != null && ((itemDsc = itemDsc.trim()).length() > 0);
         String itemPrice = request.getParameter("itemPrice").trim();
         boolean hasItemPrice = itemPrice != null && ((itemPrice = itemPrice.trim()).length() > 0);
          
         // Validate inputs
         if (!hasCategory || !category.matches("[a-zA-Z]*")) {
            out.println("<h3>Please Enter a category!, must be in characters only</h3></body></html>");
            return;
         } else if (!hasName ||!itemName.matches("[a-zA-Z]*") ) {
            out.println("<h3>Please Enter item name, must be in characters only</h3></body></html>");
            return;
         } else if (!hasItemDsc || !itemDsc.matches("^[\\w\\-\\s]+$") ) {
            out.println("<h3>Please Enter description, must be in characters only</h3></body></html>");
            return;
         }else if (!hasItemPrice || !itemPrice.matches("[0-9]+.*[0-9]")) {
            out.println("<h3>Please Enter item price!, must be a number</h3></body></html>");
            return;
         }
 
         // Display the name, email and phone (arranged in a table)
         out.println("<div class=\"jumbotron text-center\">");
         out.println("<div class=\"container\">");
         out.println("<div class=\"row\">");
         out.println("<div class=\"col\">");
         out.println("<table class='table table-dark table-striped' border='1' cellpadding='6'>");
         out.println("<tr>");
         out.println("<td>Category:</td>");
         out.println("<td>" + category + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Name</td>");
         out.println("<td>" + itemName + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Description</td>");
         out.println("<td>" + itemDsc + "</td></tr>");
         out.println("<tr>");
         out.println("<td>Price</td>");
         out.println("<td>" + itemPrice + "</td></tr>");
         out.println("</table>");
         out.println("</div>");
         float price = Float.parseFloat(itemPrice);
         int id = 0;
         int qty = 11;
         
         sqlStr = "SELECT COUNT(ITEMID) FROM PRODUCT";
            //System.out.println(sqlStr);  // for debugging
         ResultSet size = stmt.executeQuery(sqlStr);
         if(size.next()){
             id = Integer.parseInt(size.getString(1));
             id++;
         }
         
         sqlStr = "INSERT INTO PRODUCT (CATEGORY, ITEMID, ITEMNAME, ITEMDSC, ITEMPRICE, QTY) \n" +
"	VALUES ('"
                    + category + "', " + id + ", '" + itemName + "', '"
                    + itemDsc+ "', " +price + ", " + qty + ")";
            //out.println("<p>"+sqlStr+"</p>");  // for debugging
            stmt.executeUpdate(sqlStr);
            out.println("<p>item successfully added</p>");
         out.println("</div>");
         out.println("<a href='start'>Back to Search Menu</a>");
         out.println("</div>");
         out.println("</div>");
 
         
         
         out.println("</body></html>");
 
         //cart.clear();   // empty the cart

      } catch (SQLException ex) {
         //cart.clear();   // empty the cart
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
