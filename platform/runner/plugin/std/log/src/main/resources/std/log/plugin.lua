function plugin_create(internal)
    local export = { }

    function export.log(level, message)
        return internal.log(level, message)
    end

    return export
end