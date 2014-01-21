
package com.eclipsesource.rap.oauth;

import javax.servlet.http.HttpSession;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class BasicEntryPoint extends AbstractEntryPoint {

  @Override
  protected void createContents( Composite parent ) {
    final int port = RWT.getRequest().getServerPort();
    parent.setLayout( new GridLayout( 2, false ) );
    parent.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Start Google Login in Browser Widget" );

    Label label = new Label( parent, SWT.NONE );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    label.setText( "<a href=\"http://localhost:"
                   + port
                   + "/oauth\" target=\"_blank\">Link to Servlet</a>" );
    Label uiSession = new Label( parent, SWT.NONE );
    uiSession.setText( "UISession=" + RWT.getUISession().getId() );
    Label httpSession = new Label( parent, SWT.NONE );
    httpSession.setText( "HTTPSession=" + RWT.getRequest().getSession().getId() );
    final Browser browser = new Browser( parent, SWT.NONE );
    GridDataFactory.fillDefaults().grab( true, true ).span( 2, 1 ).applyTo( browser );

    browser.setBounds( 0, 0, 200, 300 );
    parent.layout();

    button.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        HttpSession session = RWT.getRequest().getSession();
        int localPort = RWT.getRequest().getLocalPort();
        String authorizationURL = new Authorization( session ).getURL( localPort );
        String authorizationJavaScriptURL = "/oauthJS";
        String googleURL = "https://www.google.com/custom";
        String url = authorizationJavaScriptURL;
        System.out.println( "open in browser widget: " + url );
        browser.setUrl( url );
      }
    } );
  }
}
