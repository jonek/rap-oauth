//@ sourceURL=signInButton.js

var loginFinished = function( authResult ) {
  if( authResult ) {
    console.log( 'authentication result from Google:' );
    console.log( authResult );
    console.log( 'calling back to "' + getTokenCallbackServiceHandlerURL() + '"' );
    console.log( httpGet( getTokenCallbackServiceHandlerURL() + '&code=' + authResult['code'] ) );
  } else {
    console.log( 'NO authentication result :-(' );
  }
}

function httpGet( theUrl ) {
  var xmlHttp = null;

  xmlHttp = new XMLHttpRequest();
  xmlHttp.open( "GET", theUrl, false );
  xmlHttp.send( null );

  return xmlHttp.responseText;
}

var options = {
  'callback' : loginFinished,
  'approvalprompt' : 'force',
  'accesstype' : 'offline',
  // https://developers.google.com/+/api/oauth#scopes
  'scope' : 'https://www.googleapis.com/auth/plus.login',
  'clientid' : '408610392900-25h5lhifm78r7o0spg9jarc978nqmve0.apps.googleusercontent.com',
  'cookiepolicy' : 'single_host_origin'
};

var cid;

function getTokenCallbackServiceHandlerURL() {
  // return location.protocol + "//" + location.hostname + ":" + location.port + "/auth?servicehandler=tokenCallback&cid=" + QueryParameters["cid"];
  return 'http://localhost:4567/auth?servicehandler=tokenCallback&cid=' + cid;
}

var handleEvent = function( event ) {
  console.log( event.widget.getText() );
  cid = event.widget.getData( "cid" );
  console.log( "signing in..." );
  gapi.auth.signIn( options );
};
