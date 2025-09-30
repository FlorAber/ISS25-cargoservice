%====================================================================================
% test_architettura description   
%====================================================================================
request( loadrequest, loadrequest(PID) ).
dispatch( doDeposit, doDeposit(X) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
request( control_product, control_product(PID) ).
dispatch( load, load(SLOT) ).
dispatch( movedone, movedone(X) ).
dispatch( movefailed, movefailed(PLANDONE,PLANTODO) ).
request( moverobot, moverobot(X,Y) ).
reply( moverobotdone, moverobotdone(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
request( getProduct, getProduct(ID) ).
reply( getProductAnswer, getProductAnswer(JSONSTRING) ).  %%for getProduct
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_cargorobot, "localhost",  "TCP", "8017").
context(ctx_test, "localhost",  "TCP", "8018").
context(ctx_basicrobot, "localhost",  "TCP", "8020").
context(ctx_productservice, "localhost",  "TCP", "8111").
 qactor( mock_productservice, ctx_test, "it.unibo.mock_productservice.Mock_productservice").
 static(mock_productservice).
  qactor( mock_basicrobot, ctx_test, "it.unibo.mock_basicrobot.Mock_basicrobot").
 static(mock_basicrobot).
  qactor( holdmanager, ctx_cargo, "it.unibo.holdmanager.Holdmanager").
 static(holdmanager).
  qactor( cargorobot, ctx_cargorobot, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( cargomanager, ctx_cargo, "it.unibo.cargomanager.Cargomanager").
 static(cargomanager).
