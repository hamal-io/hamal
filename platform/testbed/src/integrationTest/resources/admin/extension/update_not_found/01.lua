sys = require('sys')
--
err, ext = sys.extension.update('12345', {
        name = 'update-ext',
        code_id = '456',
        code_ver = 32
})
assert(err.message == 'Extension not found')
assert(err['message'] == 'Extension not found')
assert(ext == nil)
