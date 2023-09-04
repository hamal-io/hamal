sys = require('sys')

--anonymous account can not create a function

err, response = sys.func.list({})

assert(err ~= nil)
assert(response == nil)
assert(err.message == 'Account not found')