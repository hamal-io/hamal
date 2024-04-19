sys = require_plugin('std.sys')

create_hook_req = fail_on_error(sys.hooks.create({
    name = 'empty-test-hook',
}))

sys.await_completed(create_hook_req)

assert(create_hook_req.id ~= nil)
assert(create_hook_req.request_status == 'Submitted')
assert(create_hook_req.id ~= nil)
assert(create_hook_req.workspace_id == '539')
assert(create_hook_req.namespace_id == '539')

hook_req = fail_on_error(sys.hooks.get(create_hook_req.id))
assert(hook_req.id == create_hook_req.id)
assert(hook_req.name == 'empty-test-hook')