sys = require('sys')

err, create_hook_req = sys.hook.create({
    name = 'empty-test-hook',
})

sys.await_completed(create_hook_req)

assert(err == nil)

assert(create_hook_req.req_id ~= nil)
assert(create_hook_req.status == 'Submitted')
assert(create_hook_req.id ~= nil)

err, hook = sys.hook.get(create_hook_req.id)
assert(err == nil)

assert(hook.id == create_hook_req.id)
assert(hook.name == 'empty-test-hook')