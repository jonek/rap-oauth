
package com.eclipsesource.rap.oauth.demo;

import org.eclipse.swt.widgets.Display;

public class AsyncBackgroundJob implements Runnable {

  private final Runnable job;
  private final Display display;

  public AsyncBackgroundJob( final Display display, Runnable job ) {
    this.display = display;
    this.job = job;
  }

  @Override
  public void run() {
    display.asyncExec( job );
  }

  public void start() {
    Thread bgThread = new Thread( this );
    bgThread.setDaemon( true );
    bgThread.start();
  }

}
