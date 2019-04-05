<%-- 
    Document   : add
    Created on : 03-Apr-2019, 14:10:15
    Author     : jasonevans
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Add</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
          
    </head>
    <script type="text/javascript"> 
             $(function(){
             $("#nav").load("nav.jsp"); });
         </script> 
         
    <div id="nav"> </div>
    <body>
        <div class="jumbotron text-center">
            <div class="container">
                <div class="row">   
                    <h1>Add a product</h1>
                </div>
                <form method='get' action='addProduct'>
                    <table>
                        <tr>
                            <td>Enter item Category:</td>
                            <td><input type='text' name='category' /></td></tr>
                        <tr>
                    <td>Enter item name:</td>
                    <td><input type='text' name='itemName' /></td></tr>
                    <tr>
                        <td>Enter item description</td>
                        <td><input type='text' name='itemDsc' /></td></tr>
                    <tr>
                        <td>Enter item Price</td>
                        <td><input type='text' name='itemPrice' /></td></tr>
                        <tr><td><input type='submit' value='Add'></td></tr>
                    </tr>
                    </table>
        </div>
        </div>
    </body>
</html>

