
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
import com.eclipsesource.rap.oauth.demo.AsyncMessageDisplay.MessageDisplay;
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

  private void startSignInListener( Display display ) {
    final ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();
    HttpSession session = RWT.getRequest().getSession();
    session.setAttribute( SIGNIN_NOTIFIER_KEY,
                          createSuccessfullSignInHandler( display, pushSession ) );
    session.setAttribute( ASYNC_MESSAGE_DISPLAY_KEY, createAsyncMessageDisplay( display ) );
  }

  private Object createSuccessfullSignInHandler( final Display display,
                                                 final ServerPushSession pushSession )
  {
    return new AsyncBackgroundJob( display, new Runnable() {

      @Override
      public void run() {
        disposeSignInButton();
        if( !info.isDisposed() && getCredentialFromSession() != null ) {
          // update the UI
          Person me = getAuthenticatedPerson();
          log( me );
          info.setText( "<i>" + me.getDisplayName() + "</i>, you successfully signed in with RAP!" );
          info.getParent().layout();
          // close push session when finished
          pushSession.stop();
        }
      }

      private void log( Person me ) {
        try {
          System.out.println( me.toPrettyString() );
        } catch( IOException e ) {
          System.err.println( "Could not print full profile: " + e );
        }
      }

    } );
  }

  private void disposeSignInButton() {
    if( !signInButton.isDisposed() ) {
      signInButton.dispose();
    }
  }

  private Object createAsyncMessageDisplay( final Display display ) {
    return new AsyncMessageDisplay( display, new MessageDisplay() {

      @Override
      public void display( String message ) {
        disposeSignInButton();
        if( !info.isDisposed() ) {
          info.setText( "<i>" + message + "</i>" );
          info.getParent().layout();
        }
      }

    } );
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

  private Person getAuthenticatedPerson() {
    Person result = null;
    GoogleCredential credential = getCredentialFromSession();
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

  private GoogleCredential getCredentialFromSession() {
    HttpSession session = RWT.getRequest().getSession();
    GoogleCredential credential = ( GoogleCredential )session.getAttribute( GOOGLE_CREDENTIAL_KEY );
    return credential;
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
