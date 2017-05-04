(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('OfficeDialogController', OfficeDialogController);

    OfficeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Office', 'Person'];

    function OfficeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Office, Person) {
        var vm = this;

        vm.office = entity;
        vm.clear = clear;
        vm.save = save;
        vm.people = Person.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.office.id !== null) {
                Office.update(vm.office, onSaveSuccess, onSaveError);
            } else {
                Office.save(vm.office, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jbpmcuyApp:officeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
