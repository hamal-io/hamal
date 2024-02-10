sys = require_plugin('sys')

_, r = sys.namespaces.create({ name = '007' })
namespace_id = r.id

print('namespace id: ' .. namespace_id)

_, r = sys.funcs.create({
    name = 'test-func',
    namespace_id = namespace_id,
    code = [[
        print('invoked by integration')
    ]]
})

func_id = r.id
print('func id: ' .. func_id)

_, r = sys.hooks.create({ name = 'hook', namespace_id = namespace_id })
hook_id = r.id
print('hook id: ' .. hook_id)

_, r = sys.triggers.create_hook({
    func_id = func_id,
    hook_id = hook_id,
    namespace_id = namespace_id,
    name = 'hook-trigger',
    inputs = { },
})
trigger_id = r.id
print('trigger id: ' .. trigger_id)