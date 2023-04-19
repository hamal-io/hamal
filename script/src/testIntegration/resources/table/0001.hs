a = {}                  -- create a table and store its reference in `a'
k = 'x'
a[k] = 10               -- new entry, with key='x' and value=10
a[20] = 'great'         -- new entry, with key=20 and value='great'
assert(a['x'])          --> 10
k = 20
assert(a[k] == 'g')     --> 'great'
a['x'] = a['x'] + 1     -- increments entry 'x'
assert(a['x'] == 11)    --> 11