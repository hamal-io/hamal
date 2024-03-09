sys = require_plugin('sys')

err, req = sys.adhoc({ code = [[answer=42]] })
assert(err == nil)
sys.await_completed(req)

err, exec = sys.execs.get(req.id)
assert(err == nil)
assert(exec.id == req.id)
assert(#exec.inputs == 0)
