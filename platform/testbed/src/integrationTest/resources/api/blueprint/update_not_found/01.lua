sys = require('sys')
--
err, update_req = sys.blueprints.update({
    id = '123456',
    name = 'update-blueprint',
    inputs = {},
    value = [[i was updated]]
})
assert(err.message == 'Blueprint not found')
assert(err['message'] == 'Blueprint not found')
assert(blueprint == nil)