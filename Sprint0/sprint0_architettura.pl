%====================================================================================
% sprint0_architettura description   
%====================================================================================
request( storerequest, storerequest(wasteType,truckLoad) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_gui, "localhost",  "TCP", "8015").
context(ctx_wastestorage, "localhost",  "TCP", "8016").
context(ctx_monitoringdevice, "localhost",  "TCP", "8017").
context(ctx_basicrobot, "localhost",  "TCP", "8018").
context(ctx_watcher, "localhost",  "TCP", "8019").
 qactor( cargo, ctx_cargo, "it.unibo.cargo.Cargo").
 static(cargo).
  qactor( watcher, ctx_watcher, "it.unibo.watcher.Watcher").
 static(watcher).
  qactor( led, ctx_monitoringdevice, "it.unibo.led.Led").
 static(led).
  qactor( sonar, ctx_monitoringdevice, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( basicrobot, ctx_basicrobot, "it.unibo.basicrobot.Basicrobot").
 static(basicrobot).
