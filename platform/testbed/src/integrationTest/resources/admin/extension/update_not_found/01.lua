sys = require('sys')
--
err, ext = sys.extension.update('12345', {
        name = 'update-ext',
        code = [[1 +1]]
})
assert(err.message == 'Extension not found')
assert(err['message'] == 'Extension not found')
assert(ext == nil)
