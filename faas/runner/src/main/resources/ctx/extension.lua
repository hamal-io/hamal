function create_extension_factory()
    local internal = _internal
    return function()
        local export = {
            exec_id = _internal.exec_id
        }

        function export.emit(event)
            -- FIXME topic must be separate from payload
            internal.emit(event.topic, event)
        end

        return export
    end
end