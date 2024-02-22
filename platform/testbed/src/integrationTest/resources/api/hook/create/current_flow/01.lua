sys = require_plugin('sys')

create_hook_req = fail_on_error(sys.hooks.create({
    name = 'empty-test-hook',
}))

sys.await_completed(create_hook_req)

assert(create_hook_req.id ~= nil)
assert(create_hook_req.status == 'Submitted')
assert(create_hook_req.hook_id ~= nil)
assert(create_hook_req.workspace_id == '539')
assert(create_hook_req.namespace_id == '539')

hook = fail_on_error(sys.hooks.get(create_hook_req.hook_id))
assert(hook.id == create_hook_req.hook_id)
assert(hook.name == 'empty-test-hook')