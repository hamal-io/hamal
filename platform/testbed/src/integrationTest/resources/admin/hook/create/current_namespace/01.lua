sys = require('sys')

create_hook_req = fail_on_error(sys.hook.create({
    name = 'empty-test-hook',
}))

sys.await_completed(create_hook_req)

assert(create_hook_req.req_id ~= nil)
assert(create_hook_req.status == 'Submitted')
assert(create_hook_req.id ~= nil)
assert(create_hook_req.group_id == '1')
assert(create_hook_req.namespace_id == '1')

hook = fail_on_error(sys.hook.get(create_hook_req.id))
assert(hook.id == create_hook_req.id)
assert(hook.name == 'empty-test-hook')