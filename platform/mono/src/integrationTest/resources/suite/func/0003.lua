sys = require('sys')
--
err = sys.func.get('123123123')
assert(err.message == 'Func not found')
assert(err['message'] == 'Func not found')