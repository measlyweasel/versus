'use strict';

angular.module('versus.services')
    .factory('Tournament', function ($resource) {
        return $resource('/api/tournaments/:tournId', {}, {vote: {method: 'POST'}})
    });
