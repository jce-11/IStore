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
 
public class EntryServlet extends HttpServlet {
 
   private String databaseURL, username, password;
 
   @Override
   public void init(ServletConfig config) throws ServletException {
      // Retrieve the database-URL, username, password from webapp init parameters
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
         out.println("<div class=\"jumbotron text-center\">");
         out.println("<h2>Welcome to the I shop</h2>");
 
      Connection conn = null;
      Statement stmt = null;
      try {
         conn = DriverManager.getConnection(databaseURL, username, password);
         stmt = conn.createStatement();
         String sqlStr = "SELECT DISTINCT CATEGORY FROM PRODUCT";
         // System.out.println(sqlStr);  // for debugging
         ResultSet rset = stmt.executeQuery(sqlStr);
         
         out.println("<form method='get' action='search'>");
         // display all categories from db
         out.println("Choose a category <select name='category' size='1'>");
         out.println("<option value=''>Select...</option>");  
         while (rset.next()) {  // loop through categories and print each one
            String category = rset.getString("CATEGORY");
            out.println("<option value='" + category + "'>" + category + "</option>");
         }
         out.println("</select><br />");
         out.println("<p>OR</p>");
 
         // text field to search for pattern matching
         out.println("Search \"Category\" or \"Product\": <input type='text' name='search' />");
         // Submit and reset buttons
         out.println("<br /><br />");
         out.println("<input type='submit' value='SEARCH' />");
         out.println("<input type='reset' value='CLEAR' />");
         out.println("</form>");
         
         HttpSession session = request.getSession(false); // check if session exists
         if (session != null) {
            Cart cart;
            synchronized (session) {
               // Retrieve the shopping cart for this session, if any. Otherwise, create one.
               cart = (Cart) session.getAttribute("cart");
               
               if (cart != null && !cart.isEmpty()) {
                  out.println("<P><a href='cart?todo=view'>View Shopping Cart</a></p>");
               }
            }
         }
         try{
             
             String name = (String) session.getAttribute("username");
               if (!name.isEmpty()){
                   out.println("<p><a href='add.jsp'>Add a product</a></p>");
                    out.println("<p><a href='viewOrder'>View Customer Orders</a></p>");
               }
         }catch(Exception e){
             
         }
         
         out.println("</div>");
         out.println("</body></html>");
      } catch (SQLException ex) {
         out.println("<h3>Service not available. Please try again later!</h3></body></html>");
         Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
 
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}
