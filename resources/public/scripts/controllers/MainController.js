"use strict";
angular.module("versus")
    .controller("MainController", function ($scope, Tournament) {

        $scope.tournaments = Tournament.query();
        $scope.newTournament = new Tournament();

       // example of updating
       // Tournament.update({_id: "test"}, {description: 'something else'})

    });
