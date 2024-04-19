sys = require_plugin('std.sys')
--
err, hook_req = sys.hooks.get('123123123')
assert(err.message == 'Hook not found')
assert(err['message'] == 'Hook not found')
assert(hook_req == nil)