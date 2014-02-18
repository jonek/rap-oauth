
package com.eclipsesource.rap.oauth.demo;

import org.eclipse.swt.widgets.Display;

public class AsyncMessageDisplay {

  private final Display display;
  private final MessageDisplay messageDisplay;

  interface MessageDisplay {

    void display( String message );

  }

  public AsyncMessageDisplay( Display display, MessageDisplay messageDisplay ) {
    this.display = display;
    this.messageDisplay = messageDisplay;
  }

  public void showAsync( final String message ) {
    new AsyncBackgroundJob( display, new Runnable() {

      @Override
      public void run() {
        messageDisplay.display( message );
      }

    } ).start();
  }

}
