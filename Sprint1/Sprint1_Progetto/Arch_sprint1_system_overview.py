### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
evattr = {
    'color': 'darkgreen',
    'style': 'dotted'
}
with Diagram('sprint1_system_overviewArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctx_cargo', graph_attr=nodeattr):
          cargomanager=Custom('cargomanager','./qakicons/symActorWithobjSmall.png')
          cargorobot=Custom('cargorobot','./qakicons/symActorWithobjSmall.png')
          mockactor=Custom('mockactor','./qakicons/symActorWithobjSmall.png')
          holdmanager=Custom('holdmanager','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctx_productservice', graph_attr=nodeattr):
          productservice=Custom('productservice(ext)','./qakicons/externalQActor.png')
     sys >> Edge( label='sonarAlert', **evattr, decorate='true', fontcolor='darkgreen') >> cargomanager
     sys >> Edge( label='sonarok', **evattr, decorate='true', fontcolor='darkgreen') >> cargomanager
     cargorobot >> Edge( label='productloaded', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='productloaded', **evattr, decorate='true', fontcolor='darkgreen') >> mockactor
     sys >> Edge( label='productloaded', **evattr, decorate='true', fontcolor='darkgreen') >> holdmanager
     cargorobot >> Edge(color='magenta', style='solid', decorate='true', label='<engage<font color="darkgreen"> engagedone engagerefused</font> &nbsp; moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     mockactor >> Edge(color='magenta', style='solid', decorate='true', label='<loadrequest<font color="darkgreen"> loadaccepted loadrejected</font> &nbsp; >',  fontcolor='magenta') >> cargomanager
     cargomanager >> Edge(color='magenta', style='solid', decorate='true', label='<controlproduct<font color="darkgreen"> productaccepted productrejected</font> &nbsp; >',  fontcolor='magenta') >> holdmanager
     holdmanager >> Edge(color='magenta', style='solid', decorate='true', label='<getProduct<font color="darkgreen"> getProductAnswer</font> &nbsp; >',  fontcolor='magenta') >> productservice
     cargorobot >> Edge(color='blue', style='solid',  decorate='true', label='<setdirection &nbsp; >',  fontcolor='blue') >> basicrobot
     mockactor >> Edge(color='blue', style='solid',  decorate='true', label='<doDeposit &nbsp; >',  fontcolor='blue') >> cargomanager
     cargomanager >> Edge(color='blue', style='solid',  decorate='true', label='<load &nbsp; >',  fontcolor='blue') >> cargorobot
diag
