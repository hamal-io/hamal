sys = require_plugin('sys')

err, req = sys.adhoc({ code = [[answer=42]] })
assert(err == nil)
sys.await_completed(req)

err, exec = sys.execs.get(req.exec_id)
assert(err == nil)
assert(exec.id == req.exec_id)
assert(#exec.inputs == 0)
