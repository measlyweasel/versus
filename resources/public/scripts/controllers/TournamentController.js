"use strict";
angular.module("versus")
    .controller("TournamentController", function ($scope, Tournament) {
        var _private = {};

        $scope.tournaments = Tournament.query();
        $scope.newTournament = new Tournament();

       // example of updating
       // Tournament.update({_id: "test"}, {description: 'something else'})

    });
