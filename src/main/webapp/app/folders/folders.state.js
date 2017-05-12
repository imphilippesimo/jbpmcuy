(function() {
	'use strict';

	angular.module('jbpmcuyApp').config(stateConfig);

	stateConfig.$inject = [ '$stateProvider' ];

	function stateConfig($stateProvider) {
		$stateProvider.state(
				'folders',
				{
					parent : 'app',
					url : '/folders',
					data : {

						authorities : [ 'ROLE_USER' ],
						pageTitle : 'jbpmcuyApp.folders.home.title'
					},
					views : {
						'content@' : {
							templateUrl : 'app/folders/folders.html',
							controller : 'FolderController',
							controllerAs : 'vm'
						}
					},
					resolve : {
						translatePartialLoader : [ '$translate',
								'$translatePartialLoader',
								function($translate, $translatePartialLoader) {
									$translatePartialLoader.addPart('folders');
									$translatePartialLoader.addPart('global');
									return $translate.refresh();
								} ]
					}
				}).state(
				'folder-detail',
				{
					parent : 'app',
					url : '/folder/detail?instanceId&docRef&handler',
					data : {
						authorities : [ 'ROLE_USER' ],
					// pageTitle : 'jbpmcuyApp.folder.detail.title'
					},
					views : {
						'content@' : {
							templateUrl : 'app/folders/folder-detail.html',
							controller : 'FolderDetailController',
							controllerAs : 'vm'
						}
					},
					resolve : {
						translatePartialLoader : [ '$translate',
								'$translatePartialLoader',
								function($translate, $translatePartialLoader) {
									$translatePartialLoader.addPart('folders');
									return $translate.refresh();
								} ],
						previousState : [
								"$state",
								function($state) {
									var currentStateData = {
										name : $state.current.name || 'folder',
										params : $state.params,
										url : $state.href($state.current.name,
												$state.params)
									};
									return currentStateData;
								} ]
					}
				}).state(
				'folder-status',
				{
					parent : 'app',
					url : '/folder/status?instanceId&docRef&requestType',
					data : {
						authorities : [ 'ROLE_USER' ],
					// pageTitle : 'jbpmcuyApp.folder.detail.title'
					},
					views : {
						'content@' : {
							templateUrl : 'app/folders/folder-status.html',
							controller : 'FolderStatusController',
							controllerAs : 'vm'
						}
					},
					resolve : {
						translatePartialLoader : [ '$translate',
								'$translatePartialLoader',
								function($translate, $translatePartialLoader) {
									$translatePartialLoader.addPart('folders');
									return $translate.refresh();
								} ],
						previousState : [
								"$state",
								function($state) {
									var currentStateData = {
										name : $state.current.name || 'folder',
										params : $state.params,
										url : $state.href($state.current.name,
												$state.params)
									};
									return currentStateData;
								} ]
					}
				});
	}
})();
