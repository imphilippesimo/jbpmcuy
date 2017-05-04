(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('OfficeController', OfficeController);

    OfficeController.$inject = ['Office'];

    function OfficeController(Office) {

        var vm = this;

        vm.offices = [];

        loadAll();

        function loadAll() {
            Office.query(function(result) {
                vm.offices = result;
                vm.searchQuery = null;
            });
        }
    }
})();
