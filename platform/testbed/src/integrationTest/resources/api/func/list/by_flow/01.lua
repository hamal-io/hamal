sys = require_plugin('sys')

one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(two_req)

hook = fail_on_error(sys.funcs.create({ namespace_id = one_req.id, name = 'func-1' }))
sys.await_completed(hook)

count = #fail_on_error(sys.funcs.list())
assert(count == 1)

count = #fail_on_error(sys.funcs.list({}))
assert(count == 1)

count = #fail_on_error(sys.funcs.list({ namespace_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.funcs.list({ namespace_ids = { two_req.id } }))
assert(count == 0)
