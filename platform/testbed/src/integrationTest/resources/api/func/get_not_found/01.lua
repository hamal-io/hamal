sys = require('sys')
--
err, func = sys.funcs.get('123123123')
assert(err.message == 'Func not found')
assert(func == nil)