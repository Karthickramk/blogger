var userName = localStorage.getItem("userName");
var token = localStorage.getItem("token");
var bloggerModule = angular.module("BloggerHome", [ 'ngRoute' ]);
bloggerModule.factory('httpRequestInterceptor', function () {
	  return {
	    request: function (config) {
	      config.headers['X-Authorization'] = "Bearer "+token;
	      return config;
	    }
	  };
});

bloggerModule.config(function ($httpProvider) {
	  $httpProvider.interceptors.push('httpRequestInterceptor');
});
bloggerModule.controller("LoginController",function($scope,BloggerHomeService,$location){
	$scope.login = function(){
		BloggerHomeService.login($scope.loginDetails).then(function(){
			data = BloggerHomeService.getResponseMessage();
			if(data == false){
				alert("Invalid user name or password");
			}
			else{
				localStorage.setItem("token", data);
				localStorage.setItem("userName", $("#userName").val());
				window.location="home.html#/home/all";
			}
		});
	};
});
bloggerModule.controller("UserRegisterController",function($scope,BloggerHomeService,$location){
	$scope.createProfile = function(){
		user = $scope.user;
		if($scope.user.password != $scope.confirm_password){
			alert("Password and confirm password do not match.")
			return;
		}
		BloggerHomeService.createProfile(user).then(function(){
			msg = BloggerHomeService.getResponseMessage();
			if(msg == 'true' || msg == true){
	    		alert("Profile created Successfully");
	    		window.location="index.html";
	    	}
	    	else if(msg == 'false'){
	    		alert("Profile creation failed. Please retry after sometime");
	    	}
	    	else{
	    		alert(msg);
	    	}
		});
	};
	$scope.cancelRegister = function(){
		window.location="index.html";
	};
});
bloggerModule.controller("BlogContentController", function($scope,$route,BloggerHomeService,$location) {
	$scope.blogs = BloggerHomeService.listAllBlogs();
});
bloggerModule.controller("BloggerMainController", function($scope,$route,$routeParams,BloggerHomeService,$location) {
	$scope.userName = localStorage.getItem("userName");
	if($routeParams.view == 'view'){
		BloggerHomeService.populateTags().then(function(){
			availableTags = [];
			data = BloggerHomeService.getResponseMessage();
			$scope.tags = data.tags;;
		});
		BloggerHomeService.loadProfile(userName).then(function(){
			$scope.user = BloggerHomeService.getResponseMessage();
			$scope.confirm_password = $scope.user.password;
		});
	}
	else if ($routeParams.search == 'all') {
		BloggerHomeService.loadAllBlogs().then(function(){
			$scope.blogs = BloggerHomeService.listAllBlogs();
		});
	}
	else if($routeParams.search == 'search'){
		BloggerHomeService.searchBlog($routeParams.id).then(function(){
			$scope.blogs = BloggerHomeService.listAllBlogs();
		});
	}
	else if($routeParams.search == 'id'){
		BloggerHomeService.getFavBlogById($routeParams.id).then(function(){
			$scope.blogs = BloggerHomeService.listAllBlogs();
		});
	}
	$scope.reloadBlogs = function(){
		BloggerHomeService.loadAllBlogs().then(function(){
			$scope.blogs = BloggerHomeService.listAllBlogs();
		});
	};
	
	$scope.postComment = function(index){
		BloggerHomeService.postComment(index).then(function(){
			$scope.reloadBlogs();
		});
	};
	
	$scope.setFavourite = function(index){
		BloggerHomeService.setFavouriteApi(index).then(function(){
			location.reload();
		});
	};	
	$scope.setUnFavourite = function(index){
		BloggerHomeService.setUnFavouriteApi(index).then(function(){
			location.reload();
		});
	};	
	BloggerHomeService.loadFavBlogs().then(function(){
		$scope.favBlogs = BloggerHomeService.listFavBlog();
	});
	$scope.showBlogContent = function(index){
		BloggerHomeService.getFavBlogById(index).then(function(){
			$scope.blogs = BloggerHomeService.listAllBlogs();
		});
	};
	BloggerHomeService.loadMessage().then(function(){
		$scope.messages = BloggerHomeService.listMessage();
	});
	$scope.goToPage = function(page){
		window.location=page;
	};
	$scope.logout = function(){
		localStorage.clear();
		window.location="index.html";
	};
	$scope.searchBlog = function(input){
		BloggerHomeService.searchBlog(input).then(function(){
			$scope.blogs = BloggerHomeService.listAllBlogs();
		});
	};
	$scope.updateProfile = function(){
		user = $scope.user;
		if($scope.user.password != $scope.confirm_password){
			alert("Password and confirm password do not match.")
			return;
		}
		BloggerHomeService.updateProfile(user).then(function(){
			msg = BloggerHomeService.getResponseMessage();
			if(msg == 'true' || msg == true){
	    		alert("Profile updated Successfully");
	    		window.location="home.html#/home/all";
	    	}
	    	else if(msg == 'false' ||  msg == false){
	    		alert("Profile creation failed. Please retry after sometime");
	    	}
	    	else{
	    		alert(msg);
	    	}
		});
	};
	$scope.cancel = function(){
		window.location="home.html#/home/all";
	};
	$scope.createBlog = function(){
		$scope.blog.tags = $scope.blog.tags.split(",")
		blog = $scope.blog;
		BloggerHomeService.createBlog(blog).then(function(){
			window.location="home.html#/home/all";
		});
	};
	var l = window.location;
    url =  l.protocol + "//" + l.host +  "/chat";
	var eb = new EventBus(url);

	eb.onopen = function() {
		eb.registerHandler("chat.to.client", function(err, msg) {
			BloggerHomeService.loadMessage().then(function(){
	    		location.reload();
	    	});
		});
	};
	$scope.postMessage = function() {
		var data = {};
		data["messageBy"] = userName;
		data["message"] = document.getElementById("message").value
		eb.publish("chat.to.server", JSON.stringify(data));
	};
	
});
bloggerModule.service('BloggerHomeService', function($http){
	var blogData = [];
	var favblogData = [];
	var searchblogData = [];
	var message = [];
	var responseMsg;
	this.login = function(loginDetails){
		var promise = $http.post('/unprotected/login',loginDetails).success(function(response){
			responseMsg = response;
		 });
		return promise;
	};
	this.loadAllBlogs = function(){
		var promise = $http.get('/api/blog').success(function(data){
			blogData = data;
		});
		return promise;
	};
	this.setFavouriteApi = function(index){
		var data = {};
		data['id'] = document.getElementById("blogId-"+index).value;
		data['userName'] = userName;
		var promise = $http.put('/api/blog/setFav',data).success(function(data){
		});
		return promise;
	};
	this.setUnFavouriteApi = function(index){
		var data = {};
		data['id'] = document.getElementById("blogId-"+index).value;
		data['userName'] = userName;
		var promise = $http.put('/api/blog/setUnFav',data).success(function(data){
		});
		return promise;
	};
	this.loadFavBlogs = function(){
		var promise = $http.get('/api/blog/getFav/'+userName).success(function(data){
			favblogData = data;
		});
		return promise;
	};
	this.getFavBlogById = function(index){
		var id = document.getElementById("fav-"+index).value;
		var promise = $http.get('/api/blog/id/'+id).success(function(data){
			blogData = [];
			blogData.push(data);
		});
		return promise;
	};
	this.listFavBlog = function(){
		return favblogData;
	};
	this.loadMessage = function(){
		var promise = $http.get('/api/messages').success(function(data){
			message = data;
		});
		return promise;
	};
	this.listMessage = function(){
		return message;
	};
	this.postComment = function(index){
		 var data = {};
		 var comment = $("#comment-"+index).val();
		 data['comment'] = comment;
		 data['userName'] = userName;
		 data['id'] = $("#blogId-"+index).val();
		 var promise = $http.put('/api/blog',data).success(function(response){
			message = response;
		 });
		 return promise;
	};
	this.postMessage = function(){
		var data = {};
		data["messageBy"] = userName;
		data["message"] = $("#message").val();
		var promise = $http.post('/api/messages',data).success(function(response){
			message.push(response);
		 });
		return promise;
	};
	this.searchBlog = function(input){
		var tagUrl = '/api/tag/blog/'+input;
		var promise = $http.get(tagUrl).success(function(data){
			blogData = data;
		});
		return promise;
	};
	this.listAllBlogs = function(){
		return blogData;
	};
	this.updateProfile = function(user){
		var promise = $http.put("/api/user/info",user).success(function(data){
			responseMsg = data;
		});
		return promise;
	};
	this.getResponseMessage = function(){
		return responseMsg;
	};
	this.createProfile = function(user){
		var promise = $http.post("/unprotected/register",user).success(function(data){
			responseMsg = data;
		});
		return promise;
	};
	this.createBlog = function(blog){
		var promise = $http.post("/api/blog",blog).success(function(data){
			responseMsg = data;
		});
		return promise;
	};
	this.loadProfile = function(userName){
		var promise = $http.get('/api/user/info/'+userName).success(function(data){
			responseMsg = data;
		});
		return promise;
	};
	this.populateTags = function(){
		var promise = $http.get('/unprotected/tags').success(function(data){
			responseMsg = data;
		});
		return promise;
	}
});
bloggerModule.config(function($routeProvider) {
	$routeProvider.when('/home/:search/:id', {
		templateUrl : 'blogContent.html',
		controller : 'BloggerMainController'
	}).when('/home/:search', {
		templateUrl : 'blogContent.html',
		controller : 'BloggerMainController'
	}).when('/new', {
		templateUrl : 'new.html',
		controller : 'BloggerMainController'
	}).when('/profile/:view', {
		templateUrl : 'profile.html',
		controller : 'BloggerMainController'
	});
});
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

function invite(){
	var emailIds = $("invite-id").val();
}