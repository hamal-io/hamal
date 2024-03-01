function plugin_create(internal)
    local export = { }

    function export.sleep(ms)
        return internal.sleep(ms or 0)
    end
    return export
end