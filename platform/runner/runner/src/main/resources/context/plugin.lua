function plugin_create(internal)
    local export = {
        api = internal.api,
        env = internal.env,
        exec = {
            id = internal.exec_id,
            hook = internal.inputs.hook,
            endpoint = internal.inputs.endpoint,
            events = internal.inputs.events,
            inputs = internal.inputs
        },
        state = internal.state
    }

    function export.emit(event)
        local evt = event or {}
        if evt.topic == nil then
            return error("Topic not present")
        end
        local topic = evt.topic
        evt.topic = nil
        internal.emit(topic, evt)
    end

    function export.complete(req)
        if (type(req) == 'nil' or req == nil) then
            return internal.complete({ status_code = 200, result = {} })
        elseif (type(req) == 'table') then
            status_code = req.status_code or 200
            result = req.result or {}
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