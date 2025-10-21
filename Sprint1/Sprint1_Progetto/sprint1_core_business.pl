%====================================================================================
% sprint1_core_business description   
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
context(ctx_productservice, "localhost",  "TCP", "8111").
 qactor( productservice, ctx_productservice, "external").
  qactor( bro, ctx_productservice, "it.unibo.bro.Bro").
 static(bro).
  qactor( testdbloader, ctx_productservice, "it.unibo.testdbloader.Testdbloader").
 static(testdbloader).
tracing.
