sys = require('sys')

local req = fail_on_error(sys.extension.create({
    name = 'test-ext',
    code_id = '123',
    code_ver = 23
}))

sys.await_completed(req)

--assert(req.req_id ~= nil)
assert(req.status == 'Submitted')
