'use strict';

angular.module('stockApp')
    .controller('StockCtrl', function($scope, $rootScope, $timeout, CoreService, StockService) {

        $scope.periods = ["1M", "2M", "3M", "6M", "1Y", "2Y", "3Y", "4Y", "5Y"];

        $scope.newAnnotation = {
            text: ""
        };

        var chart; 

        $scope.types = ["BSE", "DJI"];

        $scope.selectedTypes = [$scope.types[0]];

        $scope.init = function() {
            $scope.onPeriodChange(0);
        };

        $scope.onTypeChange = function(index) {
            var type = $scope.types[index];
            var selectedIndex = $scope.selectedTypes.indexOf(type);
            var changed = false;
            if (selectedIndex == -1) {
                $scope.selectedTypes.push(type);
                changed = true;
            } else {
                if ($scope.selectedTypes.length > 1) {
                    $scope.selectedTypes.splice(selectedIndex, 1);
                    changed = true;
                }
            }
            if (changed) {
                fetchServerData();
            }
        }

        var fetchServerData = function() {
            var promise = StockService.listStocks($scope.selectedPeriod, $scope.selectedTypes);
            promise.then(
                function(data) {
                    console.log("success", data);
                    var types = {};
                    if ($scope.selectedTypes.indexOf("BSE") > -1) {
                        types["bse"] = "BSE";
                    }
                    if ($scope.selectedTypes.indexOf("DJI") > -1) {
                        types["dji"] = "DJI";
                    }
                    var result = StockService.prepareChartData(types, data);
                    var bse = result["bse"],
                        dji = result["dji"];
                    var annotations = result["annotation"];    
                    refreshChart(bse, dji, annotations);
                },
                function(reason) {
                    console.log('Failed:' + reason);
                }
            );
        }

        $scope.onPeriodChange = function(index) {
            if ($scope.selectedPeriod == $scope.periods[index]) {
                return;
            }
            $scope.selectedPeriod = $scope.periods[index];
            console.log("onPeriodChange:: " + $scope.selectedPeriod);
            fetchServerData();
        }

        var refreshChart = function(bse, dji, annotations) {
            var series = [];
            if (bse) {
                var s1 = {
                    data: bse,
                    name: 'BSE',
                    marker: {
                        enabled: false
                    },
                    threshold: null
                }
                series.push(s1);
            }
            if (dji) {
                var s2 = {
                    data: dji,
                    name: 'DJI',
                    marker: {
                        enabled: false
                    },
                    threshold: null
                }
                series.push(s2);
            }
            console.log("series count " + series.length);
            chart = Highcharts.chart('container', {
                chart: {
                    type: 'spline',
                    zoomType: 'x',
                    panning: true,
                    panKey: 'shift'
                },
                "colors": [
                    "#7cb5ec",
                    "#434348",
                    "#90ed7d",
                    "#f7a35c",
                    "#8085e9",
                    "#f15c80",
                    "#e4d354",
                    "#31a4a3",
                    "#f45b5b",
                    "#91e8e1",
                    "#b40f37",
                    "#398102",
                    "#d35400",
                    "#f92672"
                ],
                title: {
                    text: 'Stock'
                },

                subtitle: {
                    text: 'Click and drag in the plot area to zoom in'
                },
                annotations: annotations,
                xAxis: {
                    type: 'datetime'
                },
                yAxis: {
                    title: {
                        text: 'Price ( INR )'
                    }
                },
                legend: {
                    enabled: true
                },
                plotOptions: {
                    series: {
                        events: {
                            click: function (e) {                                    
                                console.log('add new annotation...');
                                newAnnotation(e);
                            }
                        }
                    }
                },
                series: series
            });
        }

        var newAnnotation = function(e) {
            if (! $scope.newAnnotation.text) {
                return;
            }
            var annotation = {
                labels: [{
                    point: {
                        xAxis: 0,
                        yAxis: 0,
                        x: e.point.x,
                        y: e.point.y
                    },
                    text: $scope.newAnnotation.text
                }]
            }
            var promise = StockService.addAnnotation(annotation);
            promise.then(
                function(data) {
                    console.log("added annotation", data);
                    chart.addAnnotation(annotation);
                    $scope.newAnnotation.text = "";
                },
                function(reason) {
                    console.log('annotation failed:' + reason);
                }
            );
            
        }
    });