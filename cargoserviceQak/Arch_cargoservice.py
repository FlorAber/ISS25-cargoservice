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
with Diagram('cargoserviceArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcargoservice', graph_attr=nodeattr):
          createexecutor=Custom('createexecutor','./qakicons/symActorWithobjSmall.png')
          productservice=Custom('productservice','./qakicons/symActorWithobjSmall.png')
          exec_get=Custom('exec_get','./qakicons/symActorDynamicWithobj.png')
          exec_getall=Custom('exec_getall','./qakicons/symActorDynamicWithobj.png')
          guiworker=Custom('guiworker','./qakicons/symActorWithobjSmall.png')
     createexecutor >> Edge( label='cargoevent', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     guiworker >> Edge(color='magenta', style='solid', decorate='true', label='<createProduct<font color="darkgreen"> createdProduct</font> &nbsp; getProduct<font color="darkgreen"> getProductAnswer</font> &nbsp; deleteProduct<font color="darkgreen"> deletedProduct</font> &nbsp; getAllProducts<font color="darkgreen"> getAllProductsAnswer</font> &nbsp; >',  fontcolor='magenta') >> productservice
     productservice >> Edge(color='magenta', style='dotted', decorate='true', label='<currentMsg &nbsp; >',  fontcolor='black') >> exec_getall
     productservice >> Edge(color='magenta', style='solid', decorate='true', label='<createProduct<font color="darkgreen"> createdProduct</font> &nbsp; deleteProduct<font color="darkgreen"> deletedProduct</font> &nbsp; >',  fontcolor='magenta') >> createexecutor
     productservice >> Edge(color='magenta', style='dotted', decorate='true', label='<currentMsg &nbsp; >',  fontcolor='black') >> exec_get
diag
