sys = require('sys')

_, r = sys.namespace.create({ name = '007' })
namespace_id = r.id

print('namespace id: ' .. namespace_id)

_, r = sys.func.create({
    name = 'test-func',
    namespace_id = namespace_id,
    code = [[
        print('invoked by webhook')
    ]]
})

func_id = r.id
print('func id: ' .. func_id)

_, r = sys.hook.create({ name = 'hook', namespace_id = namespace_id })
hook_id = r.id
print('hook id: ' .. hook_id)

_, r = sys.trigger.create_hook({
    func_id = func_id,
    hook_id = hook_id,
    namespace_id = namespace_id,
    name = 'hook-trigger',
    inputs = { },
})
trigger_id = r.id
print('trigger id: ' .. trigger_id)