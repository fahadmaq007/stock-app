'use strict';

describe('TodoService Unit Test', function () {

    var TodoService;
    var mockCoreService;
    var $rootScope;
    var $q;
    var deferred;

    beforeEach(module('todoApp'));

    beforeEach(function () {
        mockCoreService = {
            callService: function(url, method, pathParam,  queryParams, data) {
                deferred.resolve(data);
                return deferred.promise;
            }
        };

        module(function ($provide) {
            $provide.value('CoreService', mockCoreService);
        });
    });

    beforeEach(inject(function (_TodoService_, _$rootScope_, _$q_) {
        TodoService = _TodoService_;
        $rootScope = _$rootScope_;
        $q = _$q_;
        deferred = _$q_.defer();
    }));


    it('can get an instance of todo service', inject(function(TodoService) {
        expect(TodoService).toBeDefined();
    }));

    it('it can list todos', inject(function(TodoService) {
        TodoService.listTodos().then(function(result) {
            expect(result).toBeDefined();
        }).catch(function(error) {
            expect(error).toBe('forbidden');
        })
    }));

})

