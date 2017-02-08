$(document).ready(function() {
     $('#save').click(function() {
		 var data = {};
		 var tags = $("#tags").val().split(',');
	     data["title"] = $("#title").val();
	     data["blogContent"] = $("#post").val();
	     data["tags"] = tags;
		$.ajax({
			url : '/api/blog',
			type : 'POST',
			data: JSON.stringify(data),
			accepts: {
		        text: "application/json"
		    },
			success:function( msg ) {
				window.location="home.html";
			}
		});
	});
     $('#cancel').click(function() {
    	 window.location="home.html";
     });
});