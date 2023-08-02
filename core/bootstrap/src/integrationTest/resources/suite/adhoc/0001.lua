local sys = require('sys')
print(sys)

--local exec_id = sys.adhoc({
--    inputs = {},
--    code = [[
--        print("inner code execution")
--    ]]
--})
---- Remember: Each test is an adhoc invocation
--local execs = sys.list_execs()
--assert(#execs == 2)
--
--local exec = execs[1]
--assert(exec.id == exec_id)
--
--test.complete()