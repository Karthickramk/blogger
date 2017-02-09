var userName = localStorage.getItem("userName");
var token = localStorage.getItem("token");
$.ajaxSetup({

    beforeSend: function (xhr)
    {
       xhr.setRequestHeader("X-Authorization","Bearer "+token);        
    },
    error:function(x,e) {
	    if(x.status==401) {
	    	window.location = "index.html";
	    }
  }
});
var userName = readCookie('username');
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
function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
}

function postMessage(){
	var data = {};
	data["messageBy"] = userName;
	data["message"] = $("#message").val();
	$.ajax({
		url : '/api/messages',
		type : 'post',
		data: JSON.stringify(data),
		accepts: {
	        text: "application/json"
	    },
		success:function( jsonResponse ) {
			data = JSON.parse(jsonResponse);
			html = "<p><u>"+data.messageBy+" | "+ moment(new Date(data.messageDate)) +"</u><br />"+data.message+"</p>"
			$("#messageContainer").append(html);
			$("#message").val("");
		}
	});
}
function postComment(index){
	 var data = {};
	 var comment = $("#comment-"+index).val();
	 data['comment'] = comment;
	 data['userName'] = userName;
	 data['id'] = $("#blogId-"+index).val();
	 $.ajax({
		url : '/api/blog',
		type : 'PUT',
		data: JSON.stringify(data),
		accepts: {
	        text: "application/json"
	    },
		success:function( msg ) {
			window.location="home.html";
		}
	 });
}
function buildBlogContent(data){
	$("#blogContent").html("");
	$.each(data, function(index) {
		html = "<div class='blogbg'><input type='hidden' id='blogId-"+index+"' value='"+data[index]._id+"'/><div style='font-weight: bold; font-size: 23px;'>"+data[index].title+"</div>";
		if($.inArray(userName, data[index].usersMarkedFavourites) > -1){
			html = html + "<img title='Mark Un Favourite' style='float:right' src='images/fav-icon-selected.png' onclick='setUnFavourite("+index+")'/>";
		}
		else{
			html = html + "<img style='float:right' title='Mark Favourite' src='images/fav-icon-unselected.png' onclick='setFavourite("+index+")'/>";
		}
		html = html+"<span>"+moment(new Date(data[index].createdDate)).format('MMMM Do YYYY, h:mm:ss a')+"</span>"+
				"<p>"+data[index].blogContent+"</p> Leave a comment: <textarea id='comment-"+index+"' rows=5 cols=40></textarea><br><br><a id='btn-"+index+"' class='loginButton' onclick='javascript:postComment("+index+")'>Submit</a>";
				
		comment_data = data[index].comments;
		$.each(comment_data, function(comment_index) {
			if(comment_index == 0){
				html = html+"<h3>Comments</h3>";
			}
			html = html+"<br/>"+moment(new Date(comment_data[comment_index].commentDate)).format('MMMM Do YYYY, h:mm:ss a')+" | "+comment_data[comment_index].commentBy+" | "+comment_data[comment_index].comment;
		});
		html = html+ "<hr/>"
		$("#blogContent").append(html);    
    });
}

function createBlog(){
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
}
function cancel(){
	 window.location="home.html";
}
function cancelRegister(){
	window.location="index.html";
}
function updateProfile(){
	var data = {};
	if($('#confirm_password').val() != $('#password').val()){
		alert("Password and confirm password do not match.")
		return;
	}
	$.each($("#profile").serializeArray(), function(_, key) {
		 if(key.name != 'confirm_password'){
	  		 if(key.name == 'interest'){
	  			data[key.name] = $('select#interest').val();
	  		 }
	  		 else{
		    	 if (data.hasOwnProperty(key.name)) {
		  			data[key.name] = $.makeArray(data[key.name]);
		  			data[key.name].push(key.value);
		  		  }
		  		  else {
		  			data[key.name] = key.value;
		  		  }
	  		 }
		 }
		 
		});
	$.ajax({
        url: '/api/user/info',
        dataType: 'text',
        type: 'PUT',
        accepts: {
	        text: "application/json"
	    },
        success: function (msg) {
        	if(msg == 'true'){
        		alert("Profile updated Successfully");
        		window.location="home.html";
        	}
        	else if(msg == 'false'){
        		alert("Profile update failed. Please retry after sometime");
        	}
        	else{
        		alert(msg);
        	}
        },
        error: function (textStatus, errorThrown) {
        	alert(errorThrown);
        },
        data: JSON.stringify(data)
    });
}
function createProfile(){
	var data = {};
	if($('#confirm_password').val() != $('#password').val()){
		alert("Password and confirm password do not match.")
		return;
	}
	$.each($("#registerForm").serializeArray(), function(_, key) {
		 if(key.name != 'confirm_password'){
	  		 if(key.name == 'interest'){
	  			data[key.name] = $('select#interest').val();
	  		 }
	  		 else{
		    	 if (data.hasOwnProperty(key.name)) {
		  			data[key.name] = $.makeArray(data[key.name]);
		  			data[key.name].push(key.value);
		  		  }
		  		  else {
		  			data[key.name] = key.value;
		  		  }
	  		 }
		 }
	});
	$.ajax({
        url: '/unprotected/register',
        type: 'POST',
        dataType: 'text',
	    data: JSON.stringify(data),
        success:function(msg) {
        	if(msg == 'true'){
        		alert("Profile created Successfully");
        		window.location="index.html";
        	}
        	else if(msg == 'false'){
        		alert("Profile creation failed. Please retry after sometime");
        	}
        	else{
        		alert(msg);
        	}
        },
        error: function (textStatus, errorThrown) {
        	alert(errorThrown);
        }
        
    });
}
$(document).ready(function() {
	$("#username").text(userName);
});
function setFavourite(index){
	var data = {};
	data['id'] = $("#blogId-"+index).val();
	data['userName'] = userName;
	$.ajax({
		url : '/api/blog/setFav',
		type : 'PUT',
		data: JSON.stringify(data),
		accepts: {
			
			
			
	        text: "application/json"
	    },
		success:function( msg ) {
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			window.location="home.html";
		}
	 });
	
}
function setUnFavourite(index){
	var data = {};
	data['id'] = $("#blogId-"+index).val();
	data['userName'] = userName;
	$.ajax({
		url : '/api/blog/setUnFav',
		type : 'PUT',
		data: JSON.stringify(data),
		accepts: {
	        text: "application/json"
	    },
		success:function( msg ) {
			window.location="home.html";
		}
	 });
}
$("#search").click(function() {
	var tag = '/api/tag/blog/'+$("#search_input").val();
	$.ajax({
		url : tag,
		type : 'get',
		accept : 'application/json',
		success : function(data) {
			buildBlogContent(data);
		}
	});
});
function showBlogContent(index){
	var id = $("#fav-"+index).val();
	var tag = '/api/blog/id/'+id
	$.ajax({
		url : tag,
		type : 'get',
		accept : 'application/json',
		success : function(data) {
			popuateBlogContent(data);
		}
	});
}
function invite(){
	var emailIds = $("invite-id").val();
}
function popuateBlogContent(data){
	$("#blogContent").html("");
	html = "<div class='blogbg'><input type='hidden' id='blogId-1' value='"+data._id+"'/><div style='font-weight: bold; font-size: 23px;'>"+data.title+"</div>";
	if($.inArray(userName, data.usersMarkedFavourites) > -1){
		html = html + "<img title='Mark Un Favourite' style='float:right' src='images/fav-icon-selected.png' onclick='setUnFavourite(1)'/>";
	}
	else{
		html = html + "<img style='float:right' title='Mark Favourite' src='images/fav-icon-unselected.png' onclick='setFavourite(1)'/>";
	}
	html = html+"<span>"+moment(new Date(data.createdDate)).format('MMMM Do YYYY, h:mm:ss a')+"</span>"+
			"<p>"+data.blogContent+"</p> Leave a comment: <textarea id='comment-1' rows=5 cols=40></textarea><br><br><a id='btn-1' class='loginButton' onclick='javascript:postComment(1)'>Submit</a>";
			
	comment_data = data.comments;
	$.each(comment_data, function(comment_index) {
		if(comment_index == 0){
			html = html+"<h3>Comments</h3>";
		}
		html = html+"<br/>"+moment(new Date(comment_data[comment_index].commentDate)).format('MMMM Do YYYY, h:mm:ss a')+" | "+comment_data[comment_index].commentBy+" | "+comment_data[comment_index].comment;
	});
	html = html+ "<hr/>"
	$("#blogContent").append(html);    
}


function logout(){
	localStorage.clear();
	deleteCookie('userName');
	deleteCookie('loginToken');
	
}
function deleteCookie(name){
    document.cookie = name + '=deleted;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}