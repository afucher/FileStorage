angular.module('app',[])
  .controller('home',function($scope, $http) {
	  $http.get('/list/').success(function(data) {
		    $scope.files = data;
		  });
});