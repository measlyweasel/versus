'use strict';

angular.module('versus', [
    'ngRoute',
    'ngResource',
    'versus.services'
]).config(function ($routeProvider) {
    $routeProvider
        .when('/', {templateUrl: 'views/main.html', controller: "MainController"})
        .when('/tournament/:tournId', {templateUrl: 'views/tournament.html', controller: 'TournamentController'})
});

angular.module('versus.services',[]);