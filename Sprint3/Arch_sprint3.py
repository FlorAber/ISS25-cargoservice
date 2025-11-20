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
with Diagram('sprint3Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctx_webgui', graph_attr=nodeattr):
          holdobserver=Custom('holdobserver','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctx_cargo', graph_attr=nodeattr):
          holdmanager=Custom('holdmanager(ext)','./qakicons/externalQActor.png')
     sys >> Edge( label='holdupdated', **evattr, decorate='true', fontcolor='darkgreen') >> holdobserver
     holdobserver >> Edge(color='magenta', style='solid', decorate='true', label='<getholdstate &nbsp; >',  fontcolor='magenta') >> holdmanager
diag
