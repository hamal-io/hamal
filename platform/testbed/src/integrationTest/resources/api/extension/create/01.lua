sys = require('sys')

local req = fail_on_error(sys.extension.create({
    name = 'test-ext',
    code = [[x='hamal']]
}))

sys.await_completed(req)

assert(req.id ~= nil)
assert(req.status == 'Submitted')
assert(req.extension_id ~= nil)
