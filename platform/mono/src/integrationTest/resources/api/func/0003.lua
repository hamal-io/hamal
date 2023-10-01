admin = require('sys')
--
err = admin.func.get('123123123')
assert(err.message == 'Func not found')
assert(err['message'] == 'Func not found')