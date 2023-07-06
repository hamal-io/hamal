local empty_table = {}
assert(empty_table.length == 0)
assert(empty_table['length'] == 0)

local table = {1,2,3,4}
assert(table.length == 4)
assert(table['length'] == 4)
