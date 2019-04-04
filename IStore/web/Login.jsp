<%-- 
    Document   : Login
    Created on : 03-Apr-2019, 00:34:21
    Author     : jasonevans
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Login</title>
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
        <form method="get" action="login">
        <table class='table table-dark table-striped' border='1' cellpadding='6'>
            <tr>
                <td>Enter your username:</td>
                <td><input type='text' name='username' /></td>
            </tr>
            <tr>
            <td>Enter your password:</td>
            <td><input type='password' name='password' /></td>
          </tr>
        </table>
        <br />
        <input type="submit" value='LOGIN' />
        <input type="reset" value='CLEAR' />
        </form>
                </div>
        </div>
        </div>
    </body>
</html>
