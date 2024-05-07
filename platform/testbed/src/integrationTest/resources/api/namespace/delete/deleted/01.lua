sys = require_plugin('std.sys')

local append_req = fail_on_error(sys.namespaces.append({
    name = 'test-namespace'
}))
sys.await_completed(append_req)

local delete_req = fail_on_error(sys.namespaces.delete({
    id = append_req.id
}))
sys.await_completed(delete_req)

local namespaces = fail_on_error(sys.namespaces.list())
assert(#namespaces == 1)
assert(namespaces[1].name == 'root-namespace')
