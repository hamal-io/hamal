sys = require('sys')

local create_req = fail_on_error(sys.snippet.create({
    name = 'test-snippet',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

local update_req = fail_on_error(sys.snippet.update(create_req.id, {
    name = 'update-snippet',
    inputs = {},
    value = [[i was updated]]
}))

sys.await_completed(update_req)

local snippet = fail_on_error(sys.snippet.get(create_req.id))

assert(snippet.id == create_req.id)
assert(snippet.name == 'update-snippet')
assert(snippet.value == [[i was updated]])