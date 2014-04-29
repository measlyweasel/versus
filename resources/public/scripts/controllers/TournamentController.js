'use strict';

angular.module("versus")
    .controller("TournamentController", function ($scope, $routeParams, Tournament, Contender) {

        Tournament.get({_id: $routeParams.tournId}).$promise.then(function(tournament){
            $scope.tournament = tournament;
            $scope.firstContender = suggestContender();
            $scope.secondContender = suggestContender($scope.firstContender);
        });

        $scope.newContender = new Contender({tournId: $routeParams.tournId});

        $scope.saveNewContender = function () {
            $scope.newContender.$save();
            if (!("contenders" in $scope.tournament)) {
                $scope.tournament.contenders = {};
            }
            $scope.tournament.contenders[$scope.newContender.name] = 0;
            $scope.newContender = new Contender({tournId: $routeParams.tournId})
        };

        function suggestContender(otherContender) {
            var contenders = Object.keys($scope.tournament.contenders);
            if (typeof otherContender === 'undefined') {
                var randomIndex = Math.floor(Math.random() * contenders.length);
                return contenders[randomIndex];
            } else {
                //group contenders by their distance to the otherContender
                var contendersGroupedByDistance = {};
                angular.forEach(contenders, function (contender) {
                    if (contender == otherContender) {
                        return
                    }

                    var distance = vectorDistance(contender, otherContender);
                    if (!(distance in contendersGroupedByDistance)){
                        contendersGroupedByDistance[distance] = [];
                    }
                    contendersGroupedByDistance[distance].push(contender);
                });
                var minKey = Math.min(Object.keys(contendersGroupedByDistance));

                var randomIndex = Math.floor(Math.random() * contendersGroupedByDistance[minKey].length);
                return contendersGroupedByDistance[minKey][randomIndex];
            }
        };

        //left this open ended so additional dimensions can be added easily
        function vectorDistance(contender, otherContender) {

            var votesSquared = Math.pow(contender.value - otherContender.value, 2);

            return Math.sqrt(votesSquared);
        }

    });

