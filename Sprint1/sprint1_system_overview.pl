%====================================================================================
% sprint1_system_overview description   
%====================================================================================
dispatch( robotready, robotready(0) ).
dispatch( databaseready, databaseready(0) ).
dispatch( allready, allReady(0) ).
request( loadrequest, loadrequest(PID) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
request( controlproduct, controlproduct(PID) ).
reply( productaccepted, productaccepted(SLOT) ).  %%for controlproduct
reply( productrejected, productrejected(MSG) ).  %%for controlproduct
event( doDeposit, doDeposit(X) ).
event( waitingForDeposit, waitingfordeposit(X) ).
event( stopWaitingForDeposit, stopWaitingForDeposit(X) ).
event( productloaded, productloaded(X) ).
event( holdupdated, holdupdated(JSONSTATE) ).
request( getholdstate, getholdstate(X) ).
reply( holdstate, holdstate(JSONSTATE) ).  %%for getholdstate
request( load, load(SLOT) ).
reply( loadended, loadended(0) ).  %%for load
reply( loadfailed, productRejecteded(MSG) ).  %%for load
request( engage, engage(OWNER,STEPTIME) ).
reply( engagedone, engagedone(ARG) ).  %%for engage
reply( engagerefused, engagerefused(ARG) ).  %%for engage
request( moverobot, moverobot(TARGETX,TARGETY) ).
reply( moverobotdone, moverobotdone(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
dispatch( setdirection, dir(D) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
event( alarm, alarm(ARG) ).
request( getProduct, product(ID) ).
reply( getProductAnswer, product(JSONSTRING) ).  %%for getProduct
event( sonaralert, sonaralert(X) ).
event( sonarok, sonarok(X) ).
event( stopthesystem, stopthesystem(X) ).
event( resumethesystem, resumethesystem(X) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_basicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxproductservice, "127.0.0.1",  "TCP", "8111").
 qactor( productservice, ctxproductservice, "external").
  qactor( basicrobot, ctx_basicrobot, "external").
  qactor( cargoservice, ctx_cargo, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( cargorobot, ctx_cargo, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( holdmanager, ctx_cargo, "it.unibo.holdmanager.Holdmanager").
 static(holdmanager).
