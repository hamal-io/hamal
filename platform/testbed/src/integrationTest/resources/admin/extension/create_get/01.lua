sys = require('sys')

local req = fail_on_error(sys.extension.create({
    name = 'test-ext',
    code = [[x='hamal']]
}))

sys.await_completed(req)

assert(req.req_id ~= nil)
assert(req.status == 'Submitted')

--local ext = fail_on_error(sys.extension.get(req.id))

--assert(ext.id == req.id)
--assert(ext.name == 'test-ext')
--assert(ext.code.value == [[x='hamal']])
