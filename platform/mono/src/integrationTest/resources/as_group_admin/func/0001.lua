sys = require('sys')

err, funcs = sys.func.list()
assert(err == nil)
assert(#funcs == 0)

--err, func_one_req = sys.func.create({ name = 'func-1' })
--sys.await_completed(func_one_req)
--
--assert(err == nil)
--assert(func_one_req ~= nil)
----
--_, funcs = sys.func.list()
--assert(#funcs == 1)
--
--assert(func_one_req.id == funcs[1].id)
--assert(funcs[1].name == 'func-1')
--
--err, func_two_req = sys.func.create({ name = 'func-2' })
--sys.await_completed(func_two_req)
--
--_, funcs = sys.func.list()
--assert(#funcs == 2)
