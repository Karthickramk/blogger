$(document).ready(function() {
     $('#save').click(function() {
		 var data = {};
		 var tags = $("#tags").val().split(',');
	     data["title"] = $("#title").val();
	     data["blogContent"] = $("#post").val();
	     data["tags"] = tags;
		$.ajax({
			url : 'http://localhost:8080/api/blog',
			type : 'POST',
			data: JSON.stringify(data),
			accepts: {
		        text: "application/json"
		    }
		}).done(function( msg ) {
				window.location="home.html";
		  });
	});
     $('#cancel').click(function() {
    	 window.location="home.html";
     });
});