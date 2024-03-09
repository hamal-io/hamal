sys = require_plugin('sys')

local req = fail_on_error(sys.extensions.create({
    name = 'test-ext',
    code = [[x='hamal']]
}))

sys.await_completed(req)

assert(req.request_id ~= nil)
assert(req.request_status == 'Submitted')
assert(req.id ~= nil)
