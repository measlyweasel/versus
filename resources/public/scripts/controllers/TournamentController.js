"use strict";
angular.module("versus")
    .controller("TournamentController", function ($scope, TournamentService) {
        var _private = {};

        TournamentService.list().then(function (tournamentList) {
            $scope.tournaments = tournamentList
        });


    });
