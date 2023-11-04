sys = require('sys')

local create_req = fail_on_error(sys.extension.create({
    name = 'test-ext',
    code = [[x='hamal']]
}))

sys.await_completed(create_req)

local id = create_req.id
assert(id ~= nil)

update_req = fail_on_error(sys.extension.update({
    id = id,
    name = 'update-ext',
    code = [[hamal-updates]]
}))
sys.await_completed(update_req)

result = fail_on_error(sys.extension.get(id))

assert(result.name == 'update-ext')
assert(result.code.value == [[hamal-updates]])

update_req = fail_on_error(sys.extension.update({
    id = id,
    code = [[hamal-updates-again]]
}))
sys.await_completed(update_req)

result = fail_on_error(sys.extension.get(id))

assert(result.name == 'update-ext')
assert(result.code.value == [[hamal-updates-again]])