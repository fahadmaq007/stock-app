'use strict';

angular.module('stockApp')
    .service('StockService', function ($rootScope, $q, CoreService, UrlService, $window) {

        var self = this;

        self.listStocks = function (period, types) {
            var params = { 'period': period, 'types': types };
            var url = UrlService.stocks();
            var promise = CoreService.callService(url, 'GET', undefined, params, undefined);
            return promise;
        };

        self.prepareChartData = function (types, stocks) {
            var result = {};
            if (stocks) {
                for (var i = 0; i < stocks.length; i++) {
                    var stock = stocks[i];
                    var type = stock.type;
                    if (types[type]) {
                        var timestamp = stock.timestamp;
                        var close = stock.close;
                        var datapoint = [timestamp, close];
                        var array = result[type];
                        if (! array) {
                            array = [];
                            result[type] = array;
                        }
                        array.push(datapoint);
                    }
                    
                }
                console.log("prepareChartData", result);
            }
            return result;
        }

        self.addTodo = function (todo) {
            var data = todo;
            var url = UrlService.todos();
            var promise = CoreService.callService(url, 'PUT', undefined, undefined, data);
            return promise;
        };

        self.updateTodo = function (todo) {
            var data = todo;
            var url = UrlService.todos();
            var promise = CoreService.callService(url, 'PUT', undefined, undefined, data);
            return promise;
        };

        self.deleteTodo = function (id) {
            var pathParam = {};
            pathParam['id'] =  id;
            var url = UrlService.todo();
            var promise = CoreService.callService(url, 'DELETE', pathParam, undefined, undefined);
            return promise;
        };

    });