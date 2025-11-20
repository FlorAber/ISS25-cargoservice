%====================================================================================
% sprint3 description   
%====================================================================================
request( getholdstate, getholdstate(X) ).
event( holdupdated, holdupdated(JSONSTATE) ).
%====================================================================================
context(ctx_webgui, "localhost",  "TCP", "8025").
context(ctx_cargo, "127.0.0.1",  "TCP", "8014").
 qactor( holdmanager, ctx_cargo, "external").
  qactor( holdobserver, ctx_webgui, "it.unibo.holdobserver.Holdobserver").
 static(holdobserver).
