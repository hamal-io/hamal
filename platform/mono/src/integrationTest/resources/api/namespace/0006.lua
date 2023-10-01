admin = require('sys')

err, namespaces = admin.namespace.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)

err, namespace_req = admin.namespace.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
admin.await_completed(namespace_req)

err, namespace_req = admin.namespace.create({
    name = 'io::hamal::web3::eth'
})

assert(err == nil)
admin.await_completed(namespace_req)

err, result = admin.req.get(namespace_req.req_id)
assert(err == nil)
assert(result.status == 'Completed')
