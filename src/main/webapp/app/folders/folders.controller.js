(function() {
	'use strict';

	angular.module('jbpmcuyApp').controller('FolderController',
			FolderController);

	FolderController.$inject = [ 'CircuitsService', '$stateParams', 'Principal' ];

	function FolderController(CircuitsService, $stateParams, Principal) {

		var vm = this;
		vm.folders = [];
		vm.filterRecords = filterRecords;
		vm.completed = $stateParams.completed;

		vm.currentLogin = null;

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

		loadAll();

		function loadAll() {
			CircuitsService.getInstances().then(function(folders) {
				console.log("getting circuit instances as folders to treat");
				vm.folders = folders;
				console.log(folders);
			});

		}

		function filterRecords(folder) {
			if (!vm.completed)
				return folder.processInstanceInfo.duration == null;
			return folder.processInstanceInfo.duration != null
		}

	}
})();
