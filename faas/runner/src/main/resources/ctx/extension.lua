function create_extension_factory()
    local internal = _internal
    return function()
        local export = {
            events = _internal.events,
            exec_id = _internal.exec_id
        }

        function export.emit(topic, event)
            if topic == nil then return error("Topic not present") end

            internal.emit(topic, event or {})
        end

        return export
    end
end