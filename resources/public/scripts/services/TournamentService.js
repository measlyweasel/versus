'use strict';

angular.module('versus.services')
    .factory('Tournament', function ($resource) {
        return $resource(
            '/api/tournaments/:_id',
            {},
            {
                vote: {method: 'POST'},
                update: {method: 'PUT'}
            })
    });
