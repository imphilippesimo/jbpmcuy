(function() {
    'use strict';

    angular
        .module('jbpmcuyApp')
        .controller('DocumentController', DocumentController);

    DocumentController.$inject = ['Document'];

    function DocumentController(Document) {

        var vm = this;

        vm.documents = [];

        loadAll();

        function loadAll() {
            Document.query(function(result) {
                vm.documents = result;
                vm.searchQuery = null;
            });
        }
    }
})();
