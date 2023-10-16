sys = require('sys')

err, req = sys.adhoc({ code = [[answer=42]] })
assert(err == nil)
sys.await_completed(req)

err, exec = sys.exec.get(req.id)
assert(err == nil)
assert(exec.id == req.id)
assert(exec.code.id == nil)
assert(exec.code.version == nil)
assert(exec.code.value == [[answer=42]])
assert(#exec.inputs == 0)
