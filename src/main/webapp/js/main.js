//var result;
function doSearch() {
	
	pst = $('#pst').val();
	query = $('#query').val();
	
	$('#results').html("");
	$("#spinner").show();
	
	url = '/kostal/rest/search';
	if (pst != 'all') {
		url = url + '/' + pst;
	}
	url = url + '?q=' + query;
	
	$.getJSON(url, function(data) {
		result = data;
		
		var html = "";
		console.log(result);
		for (var i=0; i < result.length; i++) {
			console.log(result[i]);
			console.log(result[i]['_thrds']);
			if (result[i]['_thrds'].length != 0) {
			
				html = html.concat("<div>");
				html = html.concat("<span style='color:gray' ><i>Folder name: </i></span><b>" + result[i]['name'] + "</b><br/> ");
				html = html.concat("<span style='color:gray' ><i>Number of Messages: </i></span><b>" + result[i]['messNum'] + "</b> | ");
				html = html.concat("<span style='color:gray' ><i>Size of folder: </i></span><b>" + getSpaceSize(result[i]['messSize']) + "</b><br/>");
				html = html.concat("<span style='color:gray' ><i>Number of Threads: </i></span><b>" + result[i]['thrdNum'] + "</b> | ") ;
				html = html.concat("<span style='color:gray' ><i>Size of Threads (avg): </i></span><b>" + getSpaceSize(result[i]['thrdSizeMean']) + "</b> | ") ;
				html = html.concat("<span style='color:gray' ><i>Conversation time (avg): </i></span><b>" + getDuration(result[i]['thrdTimeMean']).toString() + "</b><br/><br/>") ;
				
	//			console.log(result[i]['name']);
	//			console.log(result[i]['messNum']);
	//			console.log(result[i]['messSize']);
	//			console.log(result[i]['thrdSizeMean']);
	//			console.log(result[i]['thrdNum']);
	//			console.log(result[i]['thrdTimeMean']);
	//			console.log(result[i]['_thrds'].length);
				html = html.concat("</div>");
	
				html = html.concat("<div>");
				html = html.concat("<table>");
				html = html.concat("<th>");
	//			html = html.concat("<td>" + "Topic" + "</td>");
				html = html.concat("<td><b>" + "Number of messages" + "</b></td>");
				html = html.concat("<td><b>" + "Size of Thread" + "</b></td>");
				html = html.concat("<td><b>" + "Conversation time" + "</b></td>");
				html = html.concat("</th>");
				for (var j=0; j < result[i]['_thrds'].length; j++) {
	//				console.log(result[i]['_thrds'][j]['topic']);
	//				console.log(result[i]['_thrds'][j]['messNum']);
	//				console.log(result[i]['_thrds'][j]['thrdSize']);
	//				console.log(result[i]['_thrds'][j]['thrdTime']);
	
					html = html.concat("<tr>");
					html = html.concat("<td>" + result[i]['_thrds'][j]['topic'] + "</td>");
					html = html.concat("<td>" + result[i]['_thrds'][j]['messNum'] + "</td>");
					html = html.concat("<td>" + getSpaceSize(result[i]['_thrds'][j]['thrdSize']) + "</td>");
					html = html.concat("<td>" + getDuration(result[i]['_thrds'][j]['thrdTime']) + "</td>");
					html = html.concat("</tr>");
				}
				html = html.concat("</table>");
				html = html.concat("</div>");
				html = html.concat("</br><hr size='0.5' noshade></br>");
			}
		}
		
		html = html.concat("</br></br>");
		$("#spinner").hide();
		$('#results').html(html);
	});
	return true;
};



function getDuration(timeMillis){
    var units = [
        {label:"millis",    mod:1000,},
        {label:"seconds",   mod:60,},
        {label:"minutes",   mod:60,},
        {label:"hours",     mod:24,},
        {label:"days",      mod:7,},
        {label:"weeks",     mod:52,},
    ];
    var duration = new Object();
    var x = timeMillis;
    for (var i = 0; i < units.length; i++){
        var tmp = x % units[i].mod;
        duration[units[i].label] = tmp;
        x = (x - tmp) / units[i].mod;
    }
    
    var str = "";
    if (duration.weeks != 0) {str += duration.weeks + " weeks, ";}
    if (duration.days != 0) {str += duration.days + " days, ";}
    if (duration.hours != 0) {str += duration.hours + " hours, ";}
    if (duration.minutes != 0) {str += duration.minutes + " mins, ";};
    return str;
}

function getSpaceSize(size){
    var units = [
        {label:"bytes", mod:1024,},
        {label:"KiB", mod:1024,},
        {label:"MiB", mod:1024,},
        {label:"GiB", mod:1024,},
    ];
    var spaceSize = new Object();
    var x = size;
    for (var i = 0; i < units.length; i++){
        var tmp = x % units[i].mod;
        spaceSize[units[i].label] = tmp;
        x = (x - tmp) / units[i].mod;
    }
    
    var str = "";
    console.log(spaceSize.GiB);
    if (spaceSize.GiB != 0) {str += spaceSize.GiB + " GiB, ";}
    if (spaceSize.MiB != 0) {str += spaceSize.MiB + " MiB, ";}
    if (spaceSize.KiB != 0) {str += spaceSize.KiB + " KiB, ";}
    return str;
}

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
