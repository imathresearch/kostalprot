$(function() {
$.getJSON('node/_cluster/nodes/_local', function(data) {
console.log(data);
/* 
var node_id = Object.keys(data.nodes)[0]
, cluster_name = data['cluster_name']
, node_name = data.nodes[node_id]['name']
, host_name = data.nodes[node_id]['hostname'];
 
$('#cluster_name').html( cluster_name);
$('#node_name').html( node_name );
$('#host_name').html( host_name );
*/
})
});