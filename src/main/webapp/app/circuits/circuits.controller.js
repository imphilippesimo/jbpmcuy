(function() {
	'use strict';

	angular.module('jbpmcuyApp').controller('CircuitsController',
			CircuitsController);

	CircuitsController.$inject = [ 'Principal', 'Auth', 'CircuitsService' ];

	function CircuitsController(Principal, Auth, CircuitsService) {
		var vm = this;

		vm.error = null;
		vm.send = send;
		vm.currentCircuit = null;
		vm.currentDoc=null;
		vm.currentLogin = null;
		
		vm.success = null;
		vm.circuits = [];

		/**
		 * Store the "circuits account" in a separate variable, and not in the
		 * shared "account" variable.
		 */
		var copyAccount = function(account) {
			return {
				login : account.login
			};
		};

		Principal.identity().then(function(account) {
			vm.currentLogin = copyAccount(account).login;
		});

		CircuitsService.getAll().then(function(circuits) {
			console.log("getting circuits types from server");			
			vm.circuits = circuits;
			console.log(circuits);
		});

		function send() {
			console.log("Starting selected circuit: "+vm.currentCircuit.circuitId+ " with doc = "+vm.currentDoc.docRef);
			
			CircuitsService.startCircuit(vm.currentCircuit.circuitId,vm.currentDoc.docRef).then(
					function() {
						vm.success = 'OK';
					}).catch(function() {
		                vm.success = null;		                
		            });
		}

	}
})();
