<%-- 
    Document   : nav
    Created on : 03-Apr-2019, 14:38:00
    Author     : jasonevans
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="java.util.*, mypkg.Cart"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</head>
<body>
<nav class="navbar navbar-expand-xl bg-dark navbar-dark">
  <!-- Brand/logo -->
  <a class="navbar-brand" href="start">IShop</a>
  
  <!-- Links -->
  <ul class="navbar-nav mr-auto">
    <li class="nav-item">
      <a class="nav-link" href="http://localhost:8080/MyShop/search?category=TABLET&search=">Tablets</a>
      
    </li>
    <li class="nav-item">
      <a class="nav-link" href="http://localhost:8080/MyShop/search?category=PHONE&search=">Phones</a>
    </li>
    
    
  </ul>
  <% 
                 String name=(String)session.getAttribute("username");
                 if(name != null){
                     
                     out.print("<a class='nav-link' >Welcome "+ name+"</a>");
                     out.print("<a class='nav-link' href='logout' >Logout</a>");
                     
                 }else if(name == null){
                     out.print("<a class='nav-link' href='Login.jsp' >Login</a>");
                 }
                 
                 
                
                %> 
                <%
                    Cart cart = null;
                    try{
                        cart = (Cart)session.getAttribute("cart");
                        if(!cart.isEmpty()){
                            out.println("<a class='nav-link' href='cart?todo=view'>Cart</a>");
                            }
                    }catch(Exception e){
                        
                    }
                    
                    %>
  
  
  
</nav>
