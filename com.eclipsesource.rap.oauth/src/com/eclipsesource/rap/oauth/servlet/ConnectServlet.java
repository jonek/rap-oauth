
package com.eclipsesource.rap.oauth.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eclipsesource.rap.oauth.Authorization;

@SuppressWarnings("serial")
public class ConnectServlet extends HttpServlet {

  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException
  {
    // response.sendRedirect( "http://google.de" );

    response.sendRedirect( new Authorization( request.getSession() ).getURL( request.getLocalPort() ) );
  }

}
