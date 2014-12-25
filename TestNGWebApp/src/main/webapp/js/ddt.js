
var tempFileCsv = null;

function handleCheckDDT() {
	if (isAPIAvailable()) {
		// $('#filebutton').bind('change', handleFileSelect);
		$(document).on('change', '.btn-file :file', function() {
			  var input = $(this),
			      numFiles = input.get(0).files ? input.get(0).files.length : 1,
			      label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
			  input.trigger('fileselect', [numFiles, label]);
		});

		
		$('.btn-file :file').on('fileselect', function(event, numFiles, label) {
			        
	        var input = $(this).parents('.input-group').find(':text'),
            log = numFiles > 1 ? numFiles + ' files selected' : label;
	        
	        if( input.length ) {
	            input.val(log);
	        } else {
	            if( log ) alert(log);
	        }
	        handleFileSelect(event);
			        
	    });
	}		
}	
function isAPIAvailable() {
	// Check for the various File API support.
	if (window.File && window.FileReader && window.FileList && window.Blob) {
		// Great success! All the File APIs are supported.
		return true;
	} else {
		// source: File API availability - http://caniuse.com/#feat=fileapi
		// source: <output> availability -
		// http://html5doctor.com/the-output-element/
		document
				.writeln('The HTML5 APIs used in this form are only available in the following browsers:<br />');
		// 6.0 File API & 13.0 <output>
		document.writeln(' - Google Chrome: 13.0 or later<br />');
		// 3.6 File API & 6.0 <output>
		document.writeln(' - Mozilla Firefox: 6.0 or later<br />');
		// 10.0 File API & 10.0 <output>
		document
				.writeln(' - Internet Explorer: Not supported (partial support expected in 10.0)<br />');
		// ? File API & 5.1 <output>
		document.writeln(' - Safari: Not supported<br />');
		// ? File API & 9.2 <output>
		document.writeln(' - Opera: Not supported');
		return false;
	}
}

	
function handleFileSelect(evt) {
	var files = evt.target.files; // FileList object
	tempFileCsv = files[0];

	// read the file metadata
	var output = ''
	output += '<span style="font-weight:bold;">' + escape(file.name)
			+ '</span><br />\n';
	output += ' - FileType: ' + (file.type || 'n/a') + '<br />\n';
	output += ' - FileSize: ' + file.size + ' bytes<br />\n';
	output += ' - LastModified: '
			+ (file.lastModifiedDate ? file.lastModifiedDate
					.toLocaleDateString() : 'n/a') + '<br />\n';

	
}

function saveCsvToNode(id) {
	
	var reader = new FileReader();
	reader.readAsText(tempFileCsv);
	reader.onload = function(event) {
		var csv = event.target.result;
		var csvData = $.csv.toArrays(csv);
		var data = {};
		data.ddt = {};
		data.ddt.csvData = csvData;
		data.ddt.csvFile = tempFileCsv.name;
		
		ref = $('#jstree_scenario_builder').jstree(true);
		ref.get_node(id).data = data;
	};
	reader.onerror = function() {
		alert('Unable to read ' + file.fileName);
	};
}
