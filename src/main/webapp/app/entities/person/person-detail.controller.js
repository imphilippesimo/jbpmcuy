(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('PersonDetailController', PersonDetailController);

    PersonDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Person', 'User', 'Office'];

    function PersonDetailController($scope, $rootScope, $stateParams, previousState, entity, Person, User, Office) {
        var vm = this;

        vm.person = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jbpmcuyApp:personUpdate', function(event, result) {
            vm.person = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
