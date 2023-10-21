sys = require('sys')

err, create_req = sys.snippet.create({
    name = 'test-snippet',
    inputs = {},
    value = [[40 + 2]]
})

assert(err == nil)
sys.await_completed(create_req)

err, update_req = sys.snippet.update(create_req.id, {
    name = 'update-snippet',
    inputs = {},
    value = [[i was updated]]
})

err, snippet = sys.snippet.get(create_req.id)
assert(err == nil)

assert(snippet.id == create_req.id)
assert(snippet.name == 'update-snippet')
assert(snippet.value == [[i was updated]])