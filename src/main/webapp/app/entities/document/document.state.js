(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('document', {
            parent: 'entity',
            url: '/document',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jbpmcuyApp.document.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/document/documents.html',
                    controller: 'DocumentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('document');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('document-detail', {
            parent: 'document',
            url: '/document/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jbpmcuyApp.document.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/document/document-detail.html',
                    controller: 'DocumentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('document');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Document', function($stateParams, Document) {
                    return Document.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'document',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('document-detail.edit', {
            parent: 'document-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('document.new', {
            parent: 'document',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                docRef: null,
                                treated: null,
                                validated: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: 'document' });
                }, function() {
                    $state.go('document');
                });
            }]
        })
        .state('document.edit', {
            parent: 'document',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: 'document' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('document.delete', {
            parent: 'document',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-delete-dialog.html',
                    controller: 'DocumentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: 'document' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
