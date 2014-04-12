'use strict';

angular.module('versus', [
    'ngRoute'
]).config(function ($routeProvider) {
    $routeProvider
        .when('/', {templateUrl: 'views/main.html'})
});