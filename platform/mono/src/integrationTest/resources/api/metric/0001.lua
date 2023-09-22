sys = require('sys')
m = sys.metrics()
assert(m.time ~= 0)
assert(m.exec_completed == 0)