sys = require('sys')

err, create_snippet_req = sys.snippet.create({
    name = 'test-snippet',
    inputs = {},
    value = [[40 + 2]]
})

sys.await_completed(create_snippet_req)
assert(err == nil)

assert(create_snippet_req.req_id ~= nil)
assert(create_snippet_req.status == 'Submitted')
assert(create_snippet_req.id ~= nil)

err, snippet = sys.snippet.get(create_snippet_req.id)
assert(err == nil)

assert(snippet.id == create_snippet_req.id)
assert(snippet.name == 'test-snippet')
assert(snippet.value == [[40 + 2]])