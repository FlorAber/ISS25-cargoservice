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
with Diagram('test_architetturaArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctx_cargo', graph_attr=nodeattr):
          holdmanager=Custom('holdmanager','./qakicons/symActorWithobjSmall.png')
          cargomanager=Custom('cargomanager','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_cargorobot', graph_attr=nodeattr):
          cargorobot=Custom('cargorobot','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_test', graph_attr=nodeattr):
          mock_productservice=Custom('mock_productservice','./qakicons/symActorWithobjSmall.png')
          mock_basicrobot=Custom('mock_basicrobot','./qakicons/symActorWithobjSmall.png')
     holdmanager >> Edge(color='magenta', style='solid', decorate='true', label='<getProduct<font color="darkgreen"> getProductAnswer</font> &nbsp; >',  fontcolor='magenta') >> mock_productservice
     cargomanager >> Edge(color='magenta', style='solid', decorate='true', label='<control_product &nbsp; >',  fontcolor='magenta') >> holdmanager
     cargorobot >> Edge(color='magenta', style='solid', decorate='true', label='<moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> mock_basicrobot
     cargomanager >> Edge(color='blue', style='solid',  decorate='true', label='<load &nbsp; >',  fontcolor='blue') >> cargorobot
     cargorobot >> Edge(color='blue', style='solid',  decorate='true', label='<movedone &nbsp; movefailed &nbsp; >',  fontcolor='blue') >> cargomanager
diag
