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
with Diagram('sprint0_system_overviewArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctx_cargo', graph_attr=nodeattr):
          cargoservice=Custom('cargoservice','./qakicons/symActorWithobjSmall.png')
          cargorobot=Custom('cargorobot','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_sensor', graph_attr=nodeattr):
          led=Custom('led','./qakicons/symActorWithobjSmall.png')
          sonar=Custom('sonar','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_test', graph_attr=nodeattr):
          mockuser=Custom('mockuser','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_basicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctx_productservice', graph_attr=nodeattr):
          productservice=Custom('productservice(ext)','./qakicons/externalQActor.png')
     mockuser >> Edge(color='magenta', style='solid', decorate='true', label='<loadrequest<font color="darkgreen"> loadaccepted loadrejected</font> &nbsp; >',  fontcolor='magenta') >> cargoservice
diag
