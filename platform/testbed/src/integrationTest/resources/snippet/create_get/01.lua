sys = require('sys')

local create_req = fail_on_error(sys.snippet.create({
    name = 'test-snippet',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

assert(create_req.id ~= nil)
assert(create_req.status == 'Submitted')
assert(create_req.snippet_id ~= nil)

snippet = fail_on_error(sys.snippet.get(create_req.id))

assert(snippet.id == create_req.snippet_id)
assert(snippet.name == 'test-snippet')
assert(snippet.value == [[40 + 2]])