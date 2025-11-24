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
with Diagram('sprint1_coreArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctx_cargo', graph_attr=nodeattr):
          cargoservice=Custom('cargoservice','./qakicons/symActorWithobjSmall.png')
          cargorobot=Custom('cargorobot','./qakicons/symActorWithobjSmall.png')
          holdmanager=Custom('holdmanager','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_basicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctxproductservice', graph_attr=nodeattr):
          productservice=Custom('productservice(ext)','./qakicons/externalQActor.png')
     cargoservice >> Edge( label='waitingForDeposit', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='doDeposit', **evattr, decorate='true', fontcolor='darkgreen') >> cargoservice
     cargoservice >> Edge( label='stopWaitingForDeposit', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='productloaded', **evattr, decorate='true', fontcolor='darkgreen') >> cargoservice
     cargoservice >> Edge( label='stopthesystem', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='sonarok', **evattr, decorate='true', fontcolor='darkgreen') >> cargoservice
     cargoservice >> Edge( label='resumethesystem', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     cargorobot >> Edge( label='robotstate', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     cargorobot >> Edge( label='productloaded', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     cargorobot >> Edge( label='alarm', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='resumethesystem', **evattr, decorate='true', fontcolor='darkgreen') >> cargorobot
     holdmanager >> Edge( label='holdupdated', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='productloaded', **evattr, decorate='true', fontcolor='darkgreen') >> holdmanager
     cargoservice >> Edge(color='magenta', style='solid', decorate='true', label='<controlproduct<font color="darkgreen"> productaccepted productrejected</font> &nbsp; >',  fontcolor='magenta') >> holdmanager
     cargorobot >> Edge(color='magenta', style='solid', decorate='true', label='<engage<font color="darkgreen"> engagedone engagerefused</font> &nbsp; moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     holdmanager >> Edge(color='magenta', style='solid', decorate='true', label='<getProduct<font color="darkgreen"> getProductAnswer</font> &nbsp; >',  fontcolor='magenta') >> productservice
     cargoservice >> Edge(color='magenta', style='solid', decorate='true', label='<load<font color="darkgreen"> loadended loadfailed</font> &nbsp; >',  fontcolor='magenta') >> cargorobot
     cargorobot >> Edge(color='blue', style='solid',  decorate='true', label='<robotready &nbsp; >',  fontcolor='blue') >> cargoservice
     holdmanager >> Edge(color='blue', style='solid',  decorate='true', label='<databaseready &nbsp; >',  fontcolor='blue') >> cargoservice
diag
