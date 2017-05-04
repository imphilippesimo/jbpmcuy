(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('PersonDialogController', PersonDialogController);

    PersonDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Person', 'User', 'Office'];

    function PersonDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Person, User, Office) {
        var vm = this;

        vm.person = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.offices = Office.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.person.id !== null) {
                Person.update(vm.person, onSaveSuccess, onSaveError);
            } else {
                Person.save(vm.person, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jbpmcuyApp:personUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
