//@ sourceURL=signInButton.js

var loginFinished = function( authResult ) {
  if( authResult ) {
    console.log( 'authentication result from Google:' );
    console.log( authResult );
    sendRequest( authResult['code'] );
  } else {
    console.log( 'NO authentication result :-(' );
  }
}

function sendRequest( code ) {
  var connection = rwt.remote.Connection.getInstance();
  var request = new rwt.remote.Request( connection.getUrl(), 'GET', 'application/json' );
  request.setData( [ 'servicehandler=tokenCallback',
                     'code=' + code,
                     'cid=' + connection.getConnectionId() ].join( '&' ) );
  request.setSuccessHandler( function( event ) {
    console.log( "Request succeded", event );
  } );
  request.setErrorHandler( function( event ) {
    console.log( "Request failed", event );
  } );
  request.send();
}

var options = {
  'callback' : loginFinished,
  'approvalprompt' : 'force',
  'accesstype' : 'offline',
  // https://developers.google.com/+/api/oauth#scopes
  'scope' : 'https://www.googleapis.com/auth/plus.login',
  'clientid' : null,
  'cookiepolicy' : 'single_host_origin'
};

var handleEvent = function( event ) {
  console.log( event.widget.getText() );
  clientId = event.widget.getData( "clientId" );
  console.log( "signing in..." );
  options['clientid'] = clientId;
  gapi.auth.signIn( options );
};
