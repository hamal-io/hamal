function create_extension_factory()
    local internal = _internal
    return function()
        local export = { }

        function export.emit(event)
            -- FIXME topic must be separate from payload
            internal.emit(event.topic, event)
        end

        return export
    end
end