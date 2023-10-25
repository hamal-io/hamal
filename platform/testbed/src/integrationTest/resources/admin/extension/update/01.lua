sys = require('sys')

local create_req = fail_on_error(sys.extension.create({
    name = 'test-ext',
    code_id = '111',
    code_ver = 23
}))

sys.await_completed(create_req)

local id = create_req.id
assert(id ~= nil)

local update_req = fail_on_error(sys.extension.update(id, {
    name = 'update-ext',
    code_id = '333',
    code_ver = 32
}))

assert(err == nil)
sys.await_completed(update_req)

local result = fail_on_error(sys.extension.get(id))
assert(result ~= nil)

assert(result.id == id)
assert(result.name == 'update-ext')
assert(result.code.id == '333')
assert(result.code.version == 32)