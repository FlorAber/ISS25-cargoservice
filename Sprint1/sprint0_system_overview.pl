%====================================================================================
% sprint0_system_overview description   
%====================================================================================
request( loadrequest, loadrerequest(pid) ).
dispatch( doDeposit, doDeposit(X) ).
request( control_product, control_product(pid) ).
dispatch( do_path, do_path(PATH) ).
dispatch( move_robot, move_robot(X,Y) ).
request( does_exists, does_exists(pid) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_cargorobot, "localhost",  "TCP", "8017").
context(ctx_test, "localhost",  "TCP", "8018").
context(ctx_basicrobot, "localhost",  "TCP", "8019").
context(ctx_productservice, "localhost",  "TCP", "8020").
 qactor( productservice, ctx_productservice, "external").
  qactor( basicrobot, ctx_basicrobot, "external").
  qactor( cargomanager, ctx_cargo, "it.unibo.cargomanager.Cargomanager").
 static(cargomanager).
  qactor( cargorobot, ctx_cargorobot, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( mockactor, ctx_test, "it.unibo.mockactor.Mockactor").
 static(mockactor).
  qactor( holdmanager, ctx_cargo, "it.unibo.holdmanager.Holdmanager").
 static(holdmanager).
