sys = require('sys')
--
err, code = sys.code.get('123123123')
assert(err.message == 'Code not found')
assert(err['message'] == 'Code not found')
assert(code == nil)