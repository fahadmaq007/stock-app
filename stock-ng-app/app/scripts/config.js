'use strict';

/**
 * Configuration module.
 *
 * Values will be replaced by environment variables when run through Docker.
 */
angular.module('configuration', [])
    .constant('SERVICE_URL', 'https://mobile.dev.causeway.com');
//.constant('SERVICE_URL', 'http://localhost:81');