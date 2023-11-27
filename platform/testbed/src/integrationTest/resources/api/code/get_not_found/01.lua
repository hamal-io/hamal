sys = require_plugin('sys')
--
err, code = sys.codes.get('123123123')
assert(err.message == 'Code not found')
assert(code == nil)