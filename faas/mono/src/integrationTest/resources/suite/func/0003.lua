local sys = require('sys')
local test = require('test')
--
local error = sys.func.get('123123123')
assert(error.message == 'Func not found')
assert(error['message'] == 'Func not found')
test.complete()