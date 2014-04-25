'use strict'

angular.module('versus.services')
    .factory('Contender', function ($resource) {
        return $resource('/api/tournaments/:tournId/contenders', {tournId:'@tournId'})
    });
