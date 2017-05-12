(function() {
	'use strict';
	angular.module('jbpmcuyApp').controller('FolderStatusController',
			FolderStatusController);

	FolderStatusController.$inject = [ 'CircuitsService', '$stateParams', '$state' ];

	function FolderStatusController(CircuitsService, $stateParams, $state) {
		var vm = this;		
		vm.requestType = $stateParams.requestType;
		vm.docRef = $stateParams.docRef;
		vm.instanceId = $stateParams.instanceId;
		getPreviousSteps();
		vm.steps = null;	
		
		
		function getPreviousSteps(){
			console.log("getting completed tasks");
			CircuitsService.getCompletedTasks(vm.instanceId).then(function(steps) {				
				vm.steps = steps;
				console.log(steps);
			});

		}
		
		
	
		
		
	

	}
})();
