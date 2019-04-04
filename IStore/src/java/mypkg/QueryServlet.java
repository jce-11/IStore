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
 
public class QueryServlet extends HttpServlet {
 
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
         // Retrieve and process request parameters: "author" and "search"
         String category = request.getParameter("category");
         boolean hasCategoryParam = category != null && !category.equals("Select...");
         String searchWord = request.getParameter("search");
         boolean hasSearchParam = searchWord != null && ((searchWord = searchWord.trim()).length() > 0);
 
         out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div  id=\"nav\"> </div><body>");
         
         out.println("<div class=\"jumbotron text-center\">");
 
         if (!hasCategoryParam && !hasSearchParam) {  // No params present
            out.println("<h3>Please select a category or enter a search term!</h3>");
            out.println("<p><a href='start'>Back to Select Menu</a></p>");
         } else {
            conn = DriverManager.getConnection(databaseURL, username, password);
            stmt = conn.createStatement();
 
            // Form a SQL command based on the param(s) present
            StringBuilder sqlStr = new StringBuilder();  // more efficient than String
            sqlStr.append("SELECT * FROM PRODUCT WHERE (");
            if (hasCategoryParam) {
               sqlStr.append("CATEGORY = '").append(category).append("'");
            }
            if (hasSearchParam) {
               if (hasCategoryParam) {
                  sqlStr.append(" OR ");
               }
               sqlStr.append("CATEGORY LIKE '%").append(searchWord)
                     .append("%' OR ITEMNAME LIKE '%").append(searchWord).append("%'");
            }
            sqlStr.append(") ORDER BY CATEGORY");
            //System.out.println(sqlStr);  // for debugging
            ResultSet rset = stmt.executeQuery(sqlStr.toString());
 
            if (!rset.next()) {  // Check for empty ResultSet if this is empty no product has been found
               out.println("<h3>No product found. Please try again!</h3>");
               out.println("<p><a href='start'>Back to Select Menu</a></p>");
            } else {
               // print the table using html
               out.println("<h1>"+ category + "</h1>");
               out.println("<form method='get' action='cart'>");
               out.println("<input type='hidden' name='todo' value='add' />");
               out.println("<table class='table table-dark table-striped' border='1' cellpadding='6'>");
               out.println("<tr>");
               out.println("<th>&nbsp;</th>");
               out.println("<th>Category</th>");
               out.println("<th>ITEMNAME</th>");
               out.println("<th>ITEMDSC</th>");
               out.println("<th>PRICE</th>");
               out.println("<th>QTY</th>");
               out.println("</tr>");
               int size = 0;
               // ResultSet's cursor now pointing at first row
               do {
                  // Print each row with a checkbox identified by book's id
                  String id = rset.getString("ITEMID");
                  out.println("<tr>");
                  out.println("<td><input type='checkbox' name='id' value='" + id + "' /></td>");
                  out.println("<td>" + rset.getString("CATEGORY") + "</td>");
                  out.println("<td>" + rset.getString("ITEMNAME") + "</td>");
                  out.println("<td>" + rset.getString("ITEMDSC") + "</td>");
                  out.println("<td>£" + rset.getString("ITEMPRICE") + "</td>");
                  out.println("<td><input type='text' size='3' value='1' name='qty" + id + "' /></td>");
                  out.println("</tr>");
                  size++;
               } while (rset.next());
               
               out.println("</table><br />");
               out.println("<input type='submit' value='Add to My Shopping Cart' />");
               out.println("<input type='reset' value='CLEAR' /></form>");
 
              
               // Hyperlink to go back to search menu
               out.println("<p><a href='start'>Back to Select Menu</a></p>");
               
               HttpSession session = request.getSession(false); // check if session exists
               
               if (session != null) {
                  Cart cart;
                  synchronized (session) {
                     // Retrieve the shopping cart for this session, if any. Otherwise, create one.
                     session.setAttribute("size", size);
                     cart = (Cart) session.getAttribute("cart");
                     if (cart != null && !cart.isEmpty()) {
                        out.println("<p><a href='cart?todo=view'>View Shopping Cart</a></p>");
                     }
                  }
               }

               out.println("</div>");
            }
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
