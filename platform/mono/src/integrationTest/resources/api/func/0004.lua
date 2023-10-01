admin = require('sys')
--
_, namespace_req = admin.namespace.create({ name = "hamal::name:space::rocks" })
admin.await_completed(namespace_req)

err, func_req = admin.func.create({
    namespace_id = namespace_req.id,
    name = 'func-1'
})

admin.await_completed(func_req)
assert(err == nil)
assert(func_req ~= nil)

_, func = admin.func.get(func_req.id)
assert(func.namespace.id == namespace_req.id)
assert(func.namespace.name == "hamal::name:space::rocks")
