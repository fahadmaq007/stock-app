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
                    var array = result[type];
                    if (! array) {
                        array = [];
                        result[type] = array;
                    }
                    if (type == 'annotation') {
                        array.push(stock);
                    } else if (types[type]) {
                        var timestamp = stock.timestamp;
                        var close = stock.close;
                        var datapoint = [timestamp, close];
                        array.push(datapoint);
                    }
                    
                }
                console.log("prepareChartData", result);
            }
            return result;
        }

        self.addAnnotation = function (annotation) {
            var data = angular.copy(annotation);
            data.type = 'annotation';
            var url = UrlService.stocks();
            var promise = CoreService.callService(url, 'PUT', undefined, undefined, data);
            return promise;
        };

    });