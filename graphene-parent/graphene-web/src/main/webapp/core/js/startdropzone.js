function startDropzone(){
		
		Dropzone.autoDiscover = false;
		$("#mydropzone").dropzone({
			//url: "/file/post",
			addRemoveLinks : true,
			maxFilesize: 0.5,
			dictResponseError: 'Error uploading file!'
		});
	}