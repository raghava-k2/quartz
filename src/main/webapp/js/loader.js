(function(angular) {
	'use strict';
	angular.module('gvit', []).directive('gvitLoader', function() {
		return {
			transclude : true,
			scope : {
				size : '<'
			},
			compile : function(element, attributes) {
				return {
					pre : function(scope, ele, attr) {
					}
				};
			},
			controller:function($scope, $element, $attrs){
				$element.find('.loader-class').css('height',$scope.size).css('width',$scope.size);
			},
			template : '<div class="table-class"><div class="center-class"><div class="loader-class"></div></div></div>'
		};
	});
})(angular);