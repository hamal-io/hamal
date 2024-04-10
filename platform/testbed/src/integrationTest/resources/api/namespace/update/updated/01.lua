sys = require_plugin('sys')

local append_req = fail_on_error(sys.namespaces.append({
    name = 'test-namespace'
}))
sys.await_completed(append_req)

local update_req = fail_on_error(sys.namespaces.update({
    id = append_req.id,
    name = 'namespace-renamed'
}))
sys.await_completed(update_req)

local namespaces = fail_on_error(sys.namespaces.list())

assert(#namespaces == 2)
assert(namespaces[1].name == 'root-namespace::namespace-renamed')
assert(namespaces[2].name == 'root-namespace')
