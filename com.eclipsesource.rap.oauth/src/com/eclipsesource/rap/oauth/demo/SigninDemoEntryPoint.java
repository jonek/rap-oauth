
package com.eclipsesource.rap.oauth.demo;

import static com.eclipsesource.rap.oauth.TokenCallbackServiceHandler.*;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.eclipsesource.rap.oauth.Authorization;
import com.eclipsesource.rap.oauth.ResourceLoaderUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;

public class SigninDemoEntryPoint extends AbstractEntryPoint {

  private static final String APPLICATION_NAME = "RAP OAuth Demo";
  private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

  private Label info;
  private Button signInButton;

  @Override
  protected void createContents( Composite parent ) {
    prepareParent( parent );
    loadGoogleAPIJavaScriptIntoClient();
    createHeader( parent );
    signInButton = createClientScriptingSignInButton( parent );
    info = createInfo( parent );
    startSignInListener( parent.getDisplay() );
  }

  private void startSignInListener( final Display display ) {
    final ServerPushSession pushSession = new ServerPushSession();
    Runnable bgRunnable = new Runnable() {

      @Override
      public void run() {

        display.asyncExec( new Runnable() {

          @Override
          public void run() {
            if( !info.isDisposed() ) {
              // update the UI
              signInButton.dispose();
              Person me = getAuthenticatedPersion();
              info.setText( "<i>"
                            + me.getDisplayName()
                            + "</i>, you successfully signed in with RAP!"
              // + "<br/><br/>"
              // + getFullProfile( me )
              );
              info.getParent().layout();
              // close push session when finished
              pushSession.stop();
            }
          }

        } );
      }
    };
    pushSession.start();
    RWT.getRequest().getSession().setAttribute( SIGNIN_NOTIFIER_KEY, bgRunnable );
  }

  private String getFullProfile( Person me ) {
    StringBuilder result = new StringBuilder();
    Set<Entry<String, Object>> entrySet = me.entrySet();
    for( Entry<String, Object> entry : entrySet ) {
      result.append( "<b>" );
      result.append( entry.getKey() );
      result.append( ":</b> " );
      result.append( entry.getValue() );
      result.append( "<br/>" );
    }
    return result.toString();
  }

  private Person getAuthenticatedPersion() {
    Person result = null;
    HttpSession session = RWT.getRequest().getSession();
    GoogleCredential credential = ( GoogleCredential )session.getAttribute( GOOGLE_CREDENTIAL_KEY );
    Plus plus = new Plus.Builder( TRANSPORT, JSON_FACTORY, credential ).setApplicationName( APPLICATION_NAME )
      .build();
    try {
      result = plus.people().get( "me" ).execute();
    } catch( IOException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  private void prepareParent( Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    parent.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
  }

  private void loadGoogleAPIJavaScriptIntoClient() {
    JavaScriptLoader jsLoader = RWT.getClient().getService( JavaScriptLoader.class );
    jsLoader.require( "https://apis.google.com/js/client:platform.js" );
  }

  private void createHeader( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    label.setText( "<b>Using Google OAuth with RAP</b>" );
  }

  private Label createInfo( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    return label;
  }

  private Button createClientScriptingSignInButton( Composite parent ) {
    Button signInButton = new Button( parent, SWT.PUSH );
    WidgetUtil.registerDataKeys( "clientId" );
    signInButton.setData( "clientId", Authorization.clientSecrets.getWeb().getClientId() );
    signInButton.setText( "Sign in with Google" );
    Display display = parent.getDisplay();
    signInButton.setBackground( new Color( display, 0xdd, 0x4b, 0x39 ) );
    signInButton.setForeground( display.getSystemColor( SWT.COLOR_WHITE ) );
    String scriptCode = ResourceLoaderUtil.readTextContent( "signInButton.js" );
    signInButton.addListener( SWT.Selection, new ClientListener( scriptCode ) );
    return signInButton;
  }
}
