local sys = require('sys')

--

local _, namespace_req = sys.namespace.create({ name = "hamal::name:space::rocks" })
sys.await(namespace_req)

local err, func_req = sys.func.create({
    namespace_id = namespace_req.id,
    name = 'func-1'
})

sys.await(func_req)
assert(err == nil)
assert(func_req ~= nil)

local _, func = sys.func.get(func_req.id)
assert(func.namespace.id == namespace_req.id)
assert(func.namespace.name == "hamal::name:space::rocks")
