sys = require('sys')
--
err, hook = sys.hooks.get('123123123')
assert(err.message == 'Hook not found')
assert(err['message'] == 'Hook not found')
assert(hook == nil)