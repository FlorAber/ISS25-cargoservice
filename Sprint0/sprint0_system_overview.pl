%====================================================================================
% sprint0_system_overview description   
%====================================================================================
request( loadrequest, loadrerequest(pid,wheigth) ).
dispatch( stop, stop(X) ).
dispatch( resume, resume(X) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_gui, "localhost",  "TCP", "8015").
context(ctx_sensor, "localhost",  "TCP", "8016").
context(ctx_basicrobot, "localhost",  "TCP", "8017").
 qactor( cargo_manager, ctx_cargo, "it.unibo.cargo_manager.Cargo_manager").
 static(cargo_manager).
  qactor( product_service, ctx_cargo, "it.unibo.product_service.Product_service").
 static(product_service).
  qactor( led, ctx_sensor, "it.unibo.led.Led").
 static(led).
  qactor( sonar, ctx_sensor, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( basicrobot, ctx_basicrobot, "it.unibo.basicrobot.Basicrobot").
 static(basicrobot).
