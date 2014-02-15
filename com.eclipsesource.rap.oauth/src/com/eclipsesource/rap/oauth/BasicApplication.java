
package com.eclipsesource.rap.oauth;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

import com.eclipsesource.rap.oauth.demo.SigninDemoEntryPoint;

public class BasicApplication implements ApplicationConfiguration {

  @Override
  public void configure( Application application ) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.PAGE_TITLE, "OAuth Demo" );
    application.addEntryPoint( "/auth", BasicDemoEntryPoint.class, properties );
    application.addEntryPoint( "/demo", SigninDemoEntryPoint.class, properties );
    application.addServiceHandler( "tokenCallback", new TokenCallbackServiceHandler() );
  }

}
