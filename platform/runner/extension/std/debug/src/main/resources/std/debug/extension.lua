function extension_create()
    local export = { }

    function export.create()
        local plugin = require_plugin('std.debug')

        local instance = {}

        function instance.sleep(ms)
            return plugin.sleep(ms)
        end

        return instance
    end
    return export
end