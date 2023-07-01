local funcs = {
    { name = 'test' }
}
assert(funcs[1].name == 'test')
assert(funcs[1]['name'] == 'test')