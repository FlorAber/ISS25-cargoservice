%====================================================================================
% sprint1_system_overview description   
%====================================================================================
request( createProduct, product(JSonString) ).
dispatch( doDeposit, doDeposit(X) ).
request( loadrequest, loadrequest(PID) ).
dispatch( waiting, waiting(X) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
event( productloaded, productloaded(X) ).
request( controlproduct, controlproduct(PID) ).
reply( productaccepted, productaccepted(SLOT) ).  %%for controlproduct
reply( productrejected, productrejected(MSG) ).  %%for controlproduct
dispatch( load, load(SLOT) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
reply( moverobotdone, moverobotdone(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
dispatch( setdirection, dir(D) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
request( engage, engage(OWNER,STEPTIME) ).
reply( engagedone, engagedone(ARG) ).  %%for engage
reply( engagerefused, engagerefused(ARG) ).  %%for engage
dispatch( disengage, disengage(ARG) ).
request( step, step(TIME) ).
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
request( getProduct, product(ID) ).
reply( getProductAnswer, product(JSONSTRING) ).  %%for getProduct
event( sonarAlert, sonarAlert(X) ).
event( sonarok, sonarok(X) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
context(ctx_productservice, "127.0.0.1",  "TCP", "8111").
 qactor( productservice, ctx_productservice, "external").
  qactor( basicrobot, ctxbasicrobot, "external").
  qactor( cargomanager, ctx_cargo, "it.unibo.cargomanager.Cargomanager").
 static(cargomanager).
  qactor( cargorobot, ctx_cargo, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( mockactor, ctx_cargo, "it.unibo.mockactor.Mockactor").
 static(mockactor).
  qactor( holdmanager, ctx_cargo, "it.unibo.holdmanager.Holdmanager").
 static(holdmanager).
