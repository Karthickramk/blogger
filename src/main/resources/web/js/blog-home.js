$(document).ready(function() {
	$.ajax({
		url : 'http://localhost:8080/api/blog',
		type : 'get',
		accept : 'application/json',
		success : function(data) {
			alert(data);
			$.each(data, function(index) {
	            alert(data[index]);
	            alert(data[index]);
	        });
			
		}
	});
});