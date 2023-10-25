sys = require('sys')

err, ext = sys.extension.get('12345')
assert(err.message == 'Extension not found')
assert(err['message'] == 'Extension not found')
assert(ext == nil)
