'use strict';

angular.module("versus")
    .controller("TournamentController", function ($scope, $routeParams, Tournament, Contender) {

        $scope.tournament = Tournament.get({_id: $routeParams.tournId});
        $scope.newContender = new Contender({tournId: $routeParams.tournId});

        $scope.saveNewTournament = function () {

        }

        $scope.saveNewContender = function () {
            $scope.newContender.$save();
            if (!("contenders" in $scope.tournament)) {
                $scope.tournament.contenders={};
            }
            $scope.tournament.contenders[$scope.newContender.name] = 0;
            $scope.newContender = new Contender({tournId: $routeParams.tournId})
        }

    });

