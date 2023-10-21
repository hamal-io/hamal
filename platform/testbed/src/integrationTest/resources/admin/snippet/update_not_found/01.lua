sys = require('sys')
--
err, update_req = sys.snippet.update('123456', {
    name = 'update-snippet',
    inputs = {},
    value = [[i was updated]]
})
assert(err.message == 'Snippet not found')
assert(err['message'] == 'Snippet not found')
assert(snippet == nil)