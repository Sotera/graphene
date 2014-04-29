// graphmlparse.js
// PWG for DARPA Feb 2013
// takes a graphml object with SnagL extension
// produces the "json" object that invovis expects although it isn't really json.


function graphmlParse(graph)
{
var j_nodes=[];
var j_edges=[];

	var nodes=graph.children[0].children;
	for (var i = 0; i < nodes.length; ++i) {
		var node = nodes[i];
		var id=node.attributes.id.value;
		var attrs=[];
		var data=node.children;
		for (var j = 0; j < data.length; ++j) {
			var datum=data[j];
			var key = datum.attributes[0].value;
			var val = datum.firstChild.textContent;
			attrs.push({key:key,val:val});
					
		}
		j_nodes.push({id:id,attrs:attrs});
				
	}
	var edges=graph.children[1].children;
	for (var i = 0; i < edges.length; ++i) {
		var edge = edges[i];

		var target=edge.attributes.target.textContent;
		var source=edge.attributes.source.textContent;
		j_edges.push({source:source,target:target});
	}
				
	var json=[];
	for (var i = 0; i < j_nodes.length; ++i) {
  		var node = j_nodes[i];
  		var id = node.id;
  		var adjacencies=[];
  		for (var j = 0; j < j_edges.length; ++j) {
  			var edge = j_edges[j];
  			if (edge.source == id || edge.target==id) {
		  		adjacencies.push({"nodeTo": edge.source, "nodeFrom": edge.target});
  			}
  		}
  		var color="";
  		var label="";
  		var dat={};
  		for (var k=0; k<node.attrs.length;++k) {
  			var attr = node.attrs[k];
  			if (attr.key== "label") {
  				label = attr.val;  		
			}
			else if (attr.key.indexOf ("Color") != -1) {
				switch(attr.val.length) {
					case 6: 
						color="#" + attr.val;
						break;
					case 7:
						color=attr.val;
						break;
					case 9:
						color="#" + attr.val.substring(3);
						break;
					case 8:
						color="#" + attr.val.substring(2);
						break;
				} // switch
			} // is color

  	
  		} // each attribute

  		json.push({"adjacencies": adjacencies, "data": {"$color":color,"$type": "circle", "$label":label, "attrs":node.attrs}, "id":id, "name":label});
  
  	} //each node


	return json;

}
/*
	Ext.define("Node", {
		extend:'Ext.data.Model',
		fields:['id'],
		hasMany:[
			{name: 'data', model:'NodeData'}
		]

	});


Ext.define("NodeData", {
	extend:'Ext.data.Model',
	hasMany:[
		'key',
		{name:'value', mapping:'/', type:'string'}
	],
	belongsTo:'Node'	
});

Ext.define("Edge", {
	extend:'Ext.data.Model',
	fields: ['weight','target','source'],
	belongsTo:'Edges'
});

Ext.define("Nodes", {
	extend:'Ext.data.Model',
	hasMany: [{model:'Node', name:'node'}],
	belongsTo:'Graph'	
	
});

Ext.define("Edges", {
	extend:'Ext.data.Model',
	hasMany: [{model:'Edge', name:'edge'}],
	belongsTo:'Graph'
});
*/