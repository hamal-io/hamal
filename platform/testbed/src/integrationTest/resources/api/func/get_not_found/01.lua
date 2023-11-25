sys = require('sys')
--
err, func_one = sys.funcs.get('123123123')
assert(err.message == 'Func not found')
assert(func_one == nil)