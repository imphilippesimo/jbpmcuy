(function() {
	'use strict';

	angular.module('jbpmcuyApp').factory('CircuitsService', CircuitsService);

	CircuitsService.$inject = [ '$resource' ];

	function CircuitsService($resource) {
		var service = {
			startCircuit : startCircuit,
			getInstances : getInstances,
			getAll : getAll,
			startTask : startTask,
			completeTask : completeTask,
			getCompletedTasks : getCompletedTasks

		};
		return service;

		function getAll() {
			var circuits = $resource('api/circuits', {}, {});
			return circuits.query().$promise;

		}

		function startCircuit(_circuitId, _docRef) {
			return $resource('api/circuits/start', {
				circuitId : _circuitId,
				docRef : _docRef
			}, {}).query().$promise;

		}

		function getInstances() {
			var instances = $resource('api/circuits/instances', {}, {});
			return instances.query().$promise;

		}

		function startTask(_instanceId) {
			return $resource('api/circuits/task/start', {
				instanceId : _instanceId
			}, {}).query().$promise;

		}

		function completeTask(_instanceId) {
			return $resource('api/circuits/task/complete', {
				instanceId : _instanceId
			}, {}).query().$promise;

		}

		function getCompletedTasks(_instanceId) {
			var tasks = $resource('api/circuits/task/completed', {
				instanceId : _instanceId
			}, {});
			return tasks.query().$promise;

		}
	}
})();