sys = require_plugin('sys')
--
err, ext = sys.extensions.update({
        id = '12345',
        name = 'update-ext',
        code = [[1 +1]]
})
print(err.message)
assert(err.message == 'Extension not found')
assert(err['message'] == 'Extension not found')
assert(ext == nil)
