'use strict';

angular.module('stockApp')
    .service('UrlService', function () {

        var self = this;
        
        var apiUri = 'http://localhost:8080';

        self.stocks = function () {
            return apiUri + '/stocks';
        }

        self.stock = function () {
            return self.stocks() + '/{id}';
        }
    });