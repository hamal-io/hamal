admin = require('sys')
err = admin.namespace.get('123123123')
assert(err.message == 'Namespace not found')
assert(err['message'] == 'Namespace not found')