(function() {
	'use strict';
	angular.module('jbpmcuyApp').controller('FolderDetailController',
			FolderDetailController);

	FolderDetailController.$inject = [ 'CircuitsService', '$stateParams', '$state' ];

	function FolderDetailController(CircuitsService, $stateParams, $state) {
		var vm = this;
		vm.instanceId = $stateParams.instanceId;	
		vm.docRef = $stateParams.docRef;
		vm.handler = $stateParams.handler;
		vm.success = null;
		vm.doTreatment = doTreatment;
		
		treat(vm.instanceId);
		
		// passing vm.instanceId to function causes parsing errors
		// var instanceIdAsParam = vm.instanceId;
		
		function doTreatment(){		
			console.log("Complete selected task: \n");
			console.log(vm.instanceId);
			CircuitsService.completeTask(vm.instanceId).then(function() {
				console.log('Task completed successfullly');
				vm.success = 'OK';
				$state.go('folders');
			}).catch(function() {
				vm.success = null;
			});
		}
		
		
		function treat(id){
			console.log("Starting selected task:");
			console.log("instance id= "+id);
			console.log("handler = "+ vm.handler);
			if(!vm.handler){
				CircuitsService.startTask(id).then(function() {
					console.log('Task retrieved successfully');
					vm.taskInProgress=true;
					vm.success = 'OK';
				}).catch(function() {
		            vm.success = null;		                
		        });		
			}
		}
		
		
	

	}
})();
