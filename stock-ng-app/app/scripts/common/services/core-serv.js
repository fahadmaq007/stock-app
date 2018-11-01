'use strict';

angular.module('stockApp')
    .service('CoreService', function ($q, $http, $rootScope, $window) {
        this.callService = function (url, method, pathParams, queryParams, data) {
            var queryString = '';
            if (angular.isDefined(pathParams)) {
                Object.keys(pathParams).reduce(function (previous, pathParam) {
                    var path = "{" + pathParam + "}";
                    url = url.replace(path, pathParams[pathParam]);
                }, '');
            }
            if (angular.isDefined(queryParams)) {
                queryString = Object.keys(queryParams).reduce(function (previous, key, index) {
                    return previous + ((index == 0) ? "?" : "&") + key + "=" + queryParams[key];
                }, '');
                console.log("Service Parameters: ", queryString);
            }

            var deferred = $q.defer();
            var httpOptions = {
                url: url + queryString,
                method: method,data: '',
                headers: {'Content-Type': 'application/json'}
            };
            if (data) {
                if ((method == 'PUT') || (method == 'PATCH') || (method == 'DELETE') || (method == 'POST')) {
                    httpOptions.data = data;
                }
            }

            $http(httpOptions).success(function (data) {
                deferred.resolve(data);
            }).error(function (data, status) {
                deferred.reject(data);
                console.error('Service call failed : ', data, status);
            });

            return deferred.promise;
        };

        this.handleException = function (data) {
            console.log(data);
            var exceptionClass = data.exception;
            if (exceptionClass) {
                if (exceptionClass.endsWith("BusinessException")) {
                    this.handleBusinessException(data);
                } else if (exceptionClass.endsWith("ServiceException")) {
                    this.handleServiceException(data);
                }
            }
        };
        this.handleBusinessException = function (exception) {
            console.log(exception);
            var msg = exception.detailedMessage;
            var errorType = 'Error';
            if (exception.notificationType == 'WARNING') {
                errorType = 'Warning';
            }
            toastr.error(msg, errorType, {
                closeButton: true
            });
        };
        this.handleServiceException = function (exception) {
            console.error(exception);
            var msg = exception.message;
            toastr.error(msg, 'Error', {
                closeButton: true
            });
        };

    });