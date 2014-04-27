"use strict";
angular.module("versus")
    .controller("MainController", function ($scope, Tournament) {

        $scope.tournaments = Tournament.query();
        $scope.newTournament = new Tournament();

        // example of updating
        // Tournament.update({_id: "test"}, {description: 'something else'})

        $scope.saveNewTournament = function () {
            $scope.newTournament.$save();
            $scope.tournaments.push(angular.copy($scope.newTournament))
            $scope.newTournament = new Tournament();
        }

    });
