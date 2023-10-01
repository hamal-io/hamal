admin = require('sysadmin')

err, res = admin.adhoc({
    inputs = {},
    code = [[
        print("inner code execution")
    ]]
})

--assert(err == nil)

--admin.await_completed(res)
--exec_id = res.id
--
---- Remember: Each test is an adhoc invocation
--err, execs = admin.exec.list()
--assert(err == nil)
--assert(#execs == 2)
--
--exec = execs[1]
--assert(exec.id == exec_id)