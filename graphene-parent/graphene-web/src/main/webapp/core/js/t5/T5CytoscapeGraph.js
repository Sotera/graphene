(function($) {

	T5.extendInitializers(function() {

		function init(options) {
			$('#' + options.id).cytoscape({
				style: options.style,
				layout: options.layout,
				minZoom: options.minZoom,
				maxZoom: options.maxZoom,
				elements: options.elements,
				ready: function(){
					    window.cy = this;
					    
					    // giddy up...
					    
					    cy.elements().unselectify();
					    
					    cy.on('tap', 'node', function(e){
					      var node = e.cyTarget; 
					      var neighborhood = node.neighborhood().add(node);
					      
					      cy.elements().addClass('faded');
					      neighborhood.removeClass('faded');
					    });
					    
					    cy.on('tap', function(e){
					      if( e.cyTarget === cy ){
					        cy.elements().removeClass('faded');
					      }
					    });
					  }
			});
	
		}

		return {
			cytoscapeGraph : init
		};
	});
})(jQuery);