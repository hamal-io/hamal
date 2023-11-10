sys = require('sys')

_, r = sys.flows.create({ name = '007' })
flow_id = r.id

print('flow id: ' .. flow_id)

_, r = sys.funcs.create({
    name = 'test-func',
    flow_id = flow_id,
    code = [[
        print('invoked by webhook')
    ]]
})

func_id = r.id
print('func id: ' .. func_id)

_, r = sys.hooks.create({ name = 'hook', flow_id = flow_id })
hook_id = r.id
print('hook id: ' .. hook_id)

_, r = sys.triggers.create_hook({
    func_id = func_id,
    hook_id = hook_id,
    flow_id = flow_id,
    name = 'hook-trigger',
    inputs = { },
})
trigger_id = r.id
print('trigger id: ' .. trigger_id)