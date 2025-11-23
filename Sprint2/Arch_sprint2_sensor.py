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
with Diagram('sprint2_sensorArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctx_sensor', graph_attr=nodeattr):
          sonar=Custom('sonar','./qakicons/symActorWithobjSmall.png')
          sonarmanager=Custom('sonarmanager','./qakicons/symActorWithobjSmall.png')
          led=Custom('led','./qakicons/symActorWithobjSmall.png')
     sys >> Edge( label='waitingForDeposit', **evattr, decorate='true', fontcolor='darkgreen') >> sonarmanager
     sys >> Edge( label='stopWaitingForDeposit', **evattr, decorate='true', fontcolor='darkgreen') >> sonarmanager
     sonarmanager >> Edge( label='doDeposit', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonarmanager >> Edge( label='sonaralert', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonarmanager >> Edge( label='sonarok', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='sonaralert', **evattr, decorate='true', fontcolor='darkgreen') >> led
     sys >> Edge( label='sonarok', **evattr, decorate='true', fontcolor='darkgreen') >> led
     sonar >> Edge(color='blue', style='solid',  decorate='true', label='<measurement &nbsp; >',  fontcolor='blue') >> sonarmanager
diag
