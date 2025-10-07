%====================================================================================
% sprint1_system_overview description   
%====================================================================================
request( loadrequest, loadrequest(pid) ).
dispatch( doDeposit, doDeposit(X) ).
dispatch( waiting, waiting(X) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
request( controlproduct, controlproduct(pid) ).
reply( productaccepted, productaccepted(X) ).  %%for controlproduct
reply( productjected, productjected(X) ).  %%for controlproduct
dispatch( load, load(SLOT) ).
request( moverobot, moverobot(X,Y) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
request( getProduct, product(id) ).
reply( getProductAnswer, product(JSonString) ).  %%for getProduct
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_test, "localhost",  "TCP", "8018").
context(ctx_basicrobot, "localhost",  "TCP", "8020").
context(ctxcargoservice, "localhost",  "TCP", "8111").
 qactor( productservice, ctxcargoservice, "external").
  qactor( basicrobot, ctx_basicrobot, "external").
  qactor( cargomanager, ctx_cargo, "it.unibo.cargomanager.Cargomanager").
 static(cargomanager).
  qactor( cargorobot, ctx_cargo, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( mockactor, ctx_test, "it.unibo.mockactor.Mockactor").
 static(mockactor).
  qactor( holdmanager, ctx_cargo, "it.unibo.holdmanager.Holdmanager").
 static(holdmanager).
