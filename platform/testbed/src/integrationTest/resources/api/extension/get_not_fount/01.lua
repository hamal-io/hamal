sys = require_plugin('std.sys')
--
err, ext = sys.extensions.get('12345')
assert(err.message == 'Extension not found')
assert(err['message'] == 'Extension not found')
assert(ext == nil)
