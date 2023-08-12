local sys = require('sys')
local test = require('test')
--
local error = sys.namespace.get('123123123')
assert(error.message == 'Namespace not found')
assert(error['message'] == 'Namespace not found')
test.complete()