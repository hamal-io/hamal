function plugin_create(internal)
    local export = {
        api = internal.api,
        env = internal.env,
        exec = {
            id = internal.exec_id,
            trigger_id = internal.trigger_id,
            token = internal.exec_token,
            hook = internal.inputs.hook,
            endpoint = internal.inputs.endpoint,
            events = internal.inputs.events,
            inputs = internal.inputs
        },
        state = internal.state
    }

    throw = require('std.throw').create()
    table = require('std.table').create()

    callbacks = {
        complete = { }
    }

    local function complete_functions()
        for _, fn in ipairs(callbacks.complete) do
            fn()
        end
    end

    function export.emit(event)
        local evt = event or {}
        if evt.topic == nil then
            throw.illegal_argument("Topic not present")
        end
        local topic = evt.topic
        evt.topic = nil
        internal.emit(topic, evt)
    end

    function export.on(evt, fn)
        evt = evt or throw.illegal_argument('event is not set')
        if evt ~= 'completed' then
            throw.illegal_argument(evt .. ' not supported yet')
        end

        fn = fn or throw.illegal_argument('function is not set')

        if type(fn) ~= 'function' then
            throw.illegal_argument('Expected function but got ' .. type(fn))
        end

        table.insert(callbacks.complete, fn)
    end

    function export.complete(req)
        complete_functions()

        if (type(req) == 'nil' or req == nil) then
            return internal.complete({ status_code = 200, result = {} })
        elseif (type(req) == 'table') then
            status_code = req.status_code or 200

            if req.status_code ~= nil then
                result = req.result or {}
            else
                result = req.result or req or {}
            end

            return internal.complete({ status_code = status_code, result = result })
        else
            return internal.complete({ status_code = 200, result = { value = req } })
        end
    end

    function export.fail(req)
        if (type(req) == 'nil' or req == nil) then
            return internal.fail({ status_code = 500, result = {} })
        elseif (type(req) == 'table') then
            status_code = req.status_code or 500
            result = req.result or {}
            return internal.fail({ status_code = status_code, result = result })
        else
            return internal.fail({ status_code = 500, result = { value = req } })
        end
    end

    return export
end