'use strict';

angular.module('versus', [
    'ngRoute',
    'ngResource',
    'versus.services'
]).config(function ($routeProvider) {
    $routeProvider
        .when('/', {templateUrl: 'views/main.html'})
});

angular.module('versus.services',[]);