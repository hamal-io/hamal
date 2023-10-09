sys = require('sys')

err, req = sys.adhoc({ code = [[answer=42]] })
assert(err == nil)
sys.await_completed(req)

err, exec = sys.exec.get(req.id)
assert(err == nil)
assert(exec.id == req.id)
assert(exec.code == [[answer=42]])
assert(exec.code_id == nil)
assert(exec.code_version == nil)
assert(#exec.inputs == 0)
