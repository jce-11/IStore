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
import javax.servlet.*;
import javax.servlet.http.*;
 
public class LogOutServlet extends HttpServlet {
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
 
      try {
         out.println("<html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script></head> ");
         out.println("<script type=\"text/javascript\"> $(function(){");
         out.println("$(\"#nav\").load(\"nav.jsp\"); });");
         out.println("</script> <div id=\"nav\"> </div><body>");
         
         out.println("<div class=\"jumbotron text-center\">");
         
         HttpSession session = request.getSession(false);
         if (session == null) {
            out.println("<h3>You have not login!</h3>");
         } else {
            session.invalidate();
            out.println("<p>Bye!</p>");
            out.println("<p><a href='Login.jsp'>Login</a></p>");
         }
         out.println("</body></html>");
      } finally {
         out.close();
      }
   }
}
