
		$('.fa.fa-asterisk.label.alert-info').attr({'data-content':'Term mentioned in narrative','data-original-title':'Search Term Hit'});

		$('.fa.fa-truck.label.alert-info').attr({'data-content':'Document mentions cars, trucks or motorcycles','data-original-title':'Land Vehicles'});

		$('.fa.fa-camera.label.alert-info').attr({'data-content':'Document mentions pictures or videos','data-original-title':'Media'});

		$('.fa.fa-phone-square.label.alert-info').attr({'data-content':'Document mentions phones or cell phones','data-original-title':'Phone'});

		$('.fa.fa-youtube-play.label.alert-info').attr({'data-content':'Document mentions videos or youtube','data-original-title':'Video'});

		$('.fa.fa-rss-square.label.alert-info').attr({'data-content':'Document mentions news, reports or articles','data-original-title':'News'});
		  // activate popovers with hover states
	    $("[rel=popover-hover]")
	        .popover({
	            trigger: "hover"
	        });
