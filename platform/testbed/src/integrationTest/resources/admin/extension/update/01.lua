sys = require('sys')

local create_req =  fail_on_error(sys.extension.create({
    name = 'test-ext',
    code_id = '123',
    code_ver = 23
    }))

sys.await_completed(create_req)

local update_req = fail_on_error(sys.extension.update(create_req.req_id, {
    name = 'update-ext',
    code_id = '456',
    code_ver = 32
}))

sys.await_completed(update_req)

local result = fail_on_error(sys.extension.get(create_req.req_id))

assert(result.id == create_req.req_id)
assert(result.name == 'update-ext')
assert(result.code.id == '456')
assert(result.code.version == 32)