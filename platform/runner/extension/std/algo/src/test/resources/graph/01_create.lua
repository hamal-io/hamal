algo = require('std.algo').create()

local g = algo.graph.create()
assert(type(g) == 'table')
assert(algo.graph.vertex_count(g) == 0)