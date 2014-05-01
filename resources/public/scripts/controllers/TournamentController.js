'use strict';

angular.module("versus")
    .controller("TournamentController", function ($scope, $routeParams, $cacheFactory, Tournament, Contender) {

        Tournament.get({_id: $routeParams.tournId}).$promise.then(function (tournament) {
            $scope.tournament = tournament;
            refreshContenders();
        });

        $scope.currentRoundNumber = 1;

        var contenderLastRoundSeenCache = $cacheFactory("contenderLastRoundSeen");

        $scope.newContender = new Contender({tournId: $routeParams.tournId});

        $scope.saveNewContender = function () {
            $scope.newContender.$save();
            if (!("contenders" in $scope.tournament)) {
                $scope.tournament.contenders = {};
            }
            $scope.tournament.contenders[$scope.newContender.name] = 0;
            $scope.newContender = new Contender({tournId: $routeParams.tournId})
        };

        $scope.vote = function (winner) {
            var loser = winner == $scope.firstContender ? $scope.secondContender : $scope.firstContender;
            Tournament.vote({_id: $routeParams.tournId}, {winner: winner, loser: loser});
            $scope.tournament.contenders[winner]++;
            $scope.tournament.contenders[loser]--;

            contenderLastRoundSeenCache.put(winner, $scope.currentRoundNumber);
            contenderLastRoundSeenCache.put(loser, $scope.currentRoundNumber);

            refreshContenders();
            $scope.currentRoundNumber++;
        };

        function refreshContenders() {
            $scope.firstContender = suggestContender();
            $scope.secondContender = suggestContender($scope.firstContender);
        }

        function suggestContender(otherContender) {
            var contenders = Object.keys($scope.tournament.contenders);
            if (typeof otherContender === 'undefined') {
                var randomIndex = Math.floor(Math.random() * contenders.length);
                return contenders[randomIndex];
            } else {
                //group contenders by their distance to the otherContender
                var contendersGroupedByScore = {};
                angular.forEach(contenders, function (contender) {
                    if (contender == otherContender) {
                        return
                    }

                    var score =votesScore(contender, otherContender)+ lastSeenScore(contender, otherContender);

                    console.log(otherContender + " " + contender + " -> " + score + " = " + votesScore(contender, otherContender) + " + " + lastSeenScore(contender, otherContender))
                    if (!(score in contendersGroupedByScore)) {
                        contendersGroupedByScore[score] = [];
                    }
                    contendersGroupedByScore[score].push(contender);
                });
                var keysAsInts = Object.keys(contendersGroupedByScore).map(function(el){return parseInt(el);})
                var maxKey = Math.max.apply(Math, keysAsInts);

                var randomIndex = Math.floor(Math.random() * contendersGroupedByScore[maxKey].length);
                return contendersGroupedByScore[maxKey][randomIndex];
            }
        };

        function votesScore(contender, otherContender) {
            var absoluteDifference = Math.abs($scope.tournament.contenders[contender] - $scope.tournament.contenders[otherContender])
            return parseInt(-1*absoluteDifference/Object.keys($scope.tournament.contenders).length)
        };

        function lastSeenScore(contender, otherContender) {
            var contenderLastSeen = contenderLastRoundSeenCache.get(contender)
            contenderLastSeen = contenderLastSeen ? contenderLastSeen : 99999999;

            var otherContenderLastSeen = contenderLastRoundSeenCache.get(otherContender)
            otherContenderLastSeen = otherContenderLastSeen ? otherContenderLastSeen : 0;

            return Math.abs(contenderLastSeen - otherContenderLastSeen);
        }

    });

