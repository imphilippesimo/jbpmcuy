(function() {
	'use strict';

	angular.module('jbpmcuyApp').config(stateConfig);

	stateConfig.$inject = [ '$stateProvider' ];

	function stateConfig($stateProvider) {
		$stateProvider.state('circuits', {
			parent : 'app',
			url : '/circuits',
			data : {

				authorities : [ 'ROLE_USER' ],
				pageTitle : 'jbpmcuyApp.circuits.home.title'
			},
			views : {
				'content@' : {
					templateUrl : 'app/circuits/circuits.html',
					controller : 'CircuitsController',
					controllerAs : 'vm'
				}
			},
			resolve : {
				translatePartialLoader : [ '$translate',
						'$translatePartialLoader',
						function($translate, $translatePartialLoader) {
							$translatePartialLoader.addPart('circuits');
							$translatePartialLoader.addPart('global');
							return $translate.refresh();
						} ]
			}
		});
	}
})();
