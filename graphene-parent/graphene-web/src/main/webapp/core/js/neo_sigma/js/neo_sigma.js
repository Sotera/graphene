function init() {
  // Instanciate sigma.js and customize rendering :
  var sigInst = sigma.init(document.getElementById('sigma-example')).drawingProperties({
    defaultLabelColor: '#fff',
    defaultLabelSize: 14,
    defaultLabelBGColor: '#fff',
    defaultLabelHoverColor: '#000',
    labelThreshold: 6,
    defaultEdgeType: 'curve'
  }).graphProperties({
    minNodeSize: 0.5,
    maxNodeSize: 5,
    minEdgeSize: 1,
    maxEdgeSize: 1
  }).mouseProperties({
    maxRatio: 32
  });

  // Parse a GEXF encoded file to fill the graph
  // (requires "sigma.parseGexf.js" to be included)
  sigInst.parseGexf('/neo_sigma/data/les_miserables.gexf');

  // Draw the graph :
  sigInst.draw();
  
  // Start the ForceAtlas2 algorithm
  // (requires "sigma.forceatlas2.js" to be included)
  sigInst.startForceAtlas2();
  
  var isRunning = true;
  document.getElementById('stop-layout').addEventListener('click',function(){
    if(isRunning){
      isRunning = false;
      sigInst.stopForceAtlas2();
      document.getElementById('stop-layout').childNodes[0].nodeValue = 'Start Layout';
    }else{
      isRunning = true;
      sigInst.startForceAtlas2();
      document.getElementById('stop-layout').childNodes[0].nodeValue = 'Stop Layout';
    }
  },true);
  document.getElementById('rescale-graph').addEventListener('click',function(){
    sigInst.position(0,0,1).draw();
  },true);
  
}

if (document.addEventListener) {
  document.addEventListener("DOMContentLoaded", init, false);
} else {
  window.onload = init;
}