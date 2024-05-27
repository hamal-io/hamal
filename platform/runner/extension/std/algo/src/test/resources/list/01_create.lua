-- Makes sure it is possible to create an empty list, which can hold one value

algo = require('std.algo').create()

local l = algo.list.create()
assert(type(l) == 'table')
assert(algo.list.length(l) == 0)
assert(algo.list.capacity(l) == 1)