//var result;
function doSearch() {
	
	pst = $('#pst').val();
	query = $('#query').val();
	
	url = '/kostal/rest/search';
	if (pst != 'all') {
		url = url + '/' + pst;
	}
	url = url + '?q=' + query;
	
	$.getJSON(url, function(data) {
		result = data;
		
		xx = [];
		console.log(Object.keys(result[0][0]['_source']));
		for(var i=0; i<result.length; i++){
			xx.push('<p>' + result[i][0]['_source']['conversationTopic'] + " || " + result[i][0]['_source']['clientSubmitTime'] + '</p>');
		}
			
		$('#results').html(xx.toString());
	});
	
	return true;
};


function createPstSelector() {
	url = '/kostal/rest/mapping';
	$.getJSON(url, function(data) {
		selector = data;
		
		xx = [];
//		console.log(Object.keys(result[0][0]['_source']));
		xx.push('<option value="all" selected="selected">-- All --</option>');
		for(var i=0; i<selector.length; i++){
			xx.push('<option value="' + selector[i] + '">' + selector[i] + '</option>');
		}
			
		$('#pst').html(xx.toString());
	});
	
	return true;
};
