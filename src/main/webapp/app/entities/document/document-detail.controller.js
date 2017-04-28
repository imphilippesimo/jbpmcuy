(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('DocumentDetailController', DocumentDetailController);

    DocumentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Document', 'User'];

    function DocumentDetailController($scope, $rootScope, $stateParams, previousState, entity, Document, User) {
        var vm = this;

        vm.document = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jbpmcuyApp:documentUpdate', function(event, result) {
            vm.document = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
