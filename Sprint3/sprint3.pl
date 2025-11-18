%====================================================================================
% sprint3 description   
%====================================================================================
event( productloaded, productloaded(X) ).
event( holdupdated, holdupdated(JSONSTATE) ).
%====================================================================================
context(ctx_webgui, "localhost",  "TCP", "8017").
context(ctx_cargo, "127.0.0.1",  "TCP", "8014").
 qactor( webguiobserver, ctx_webgui, "it.unibo.webguiobserver.Webguiobserver").
 static(webguiobserver).
