function plugin_factory_create()
    local internal = _internal
    return function()
        local export = { }

        function export.sleep(ms)
            return internal.sleep(ms or 0)
        end
        return export
    end
end