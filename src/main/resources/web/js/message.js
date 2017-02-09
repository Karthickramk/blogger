var userName = localStorage.getItem("userName");
function readCookie(name) {
	  var nameEQ = name + "=";
	  var ca = document.cookie.split(';');
	  for(var i = 0; i < ca.length; i++) {
	    var c = ca[i];
	    while (c.charAt(0) == ' ') c = c.substring(1, c.length);
	    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
	  }
	  return null;
}
$(document).ready(function() {
	loadFavouriteBlog();
	loadMessages();
	setInterval(function() {
		loadMessages();
	}, 1000 * 60 * 0.10);
});
function loadMessages(){
	$("#messageContainer").html(''); 
	$.ajax({
		url : '/api/messages',
		type : 'get',
		accept : 'application/json',
		success : function(jsonResponse) {
			data = JSON.parse(jsonResponse);
			$.each(data, function(index) {
				html = "<p><u>"+data[index].messageBy+" | "+ moment(new Date(data[index].messageDate)).format('MMMM Do YYYY, h:mm:ss a') +"</u><br />"+data[index].message+"</p>"
				$("#messageContainer").append(html);    
	        });
		}
	});
}

function loadFavouriteBlog(){
	$("#favBlogs").html('');
	$.ajax({
		url : '/api/blog/getFav/'+userName,
		type : 'get',
		accept : 'application/json',
		success : function(data) {
			$.each(data, function(index) {
				html =  "<li><a href='javascript:showBlogContent("+index+")'><input type='hidden' id='fav-"+index+"' value='"+data[index]._id+"'/>"+data[index].title +"</a></li>";
				$("#favBlogs").append(html);
	        });
		}
	});
}