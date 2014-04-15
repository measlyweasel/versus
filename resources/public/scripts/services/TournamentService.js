'use strict';

angular.module('versus.services')
    .factory('TournamentService', function ($http, $q) {
        var _public = {};

        _public.list = function () {
            var deferred = $q.defer();
            $http({method: 'GET', url: '/api/tournament/list'}).
                success(function(data, status, headers, config){
                    deferred.resolve(data);
                }).
                error(function(data,status,headers,config){
                    deferred.reject({error: data});
                });
            return deferred.promise;
        };


        return _public;
    });
