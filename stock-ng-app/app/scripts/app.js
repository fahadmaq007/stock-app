'use strict';

var app = angular
    .module('stockApp', [
        'ngAnimate',
        'ngRoute',
        'ui.router'
    ])
    .config(['$urlRouterProvider', '$stateProvider', '$httpProvider', function ($urlRouterProvider, $stateProvider, $httpProvider) {
        $stateProvider
            .state('stock', {
                url: '/stocks',
                templateUrl: 'scripts/stock/views/stock-chart.html',
                controller: 'StockCtrl'
            });

        $urlRouterProvider.otherwise('/stocks');
    }])
    .directive('ngEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {
                        scope.$eval(attrs.ngEnter, {'event': event});
                    });

                    event.preventDefault();
                }
            });
        };
    });
