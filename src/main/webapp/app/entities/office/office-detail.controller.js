(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('OfficeDetailController', OfficeDetailController);

    OfficeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Office', 'Person'];

    function OfficeDetailController($scope, $rootScope, $stateParams, previousState, entity, Office, Person) {
        var vm = this;

        vm.office = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jbpmcuyApp:officeUpdate', function(event, result) {
            vm.office = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
