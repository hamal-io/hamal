function extension_create()
    local log_plugin = require_plugin('std.log')
    local export = { }

    function export.create(cfg)
        local instance = {}

        function instance.trace(message)
            return log_plugin.log('Trace', message)
        end

        function instance.debug(message)
            return log_plugin.log('Debug', message)
        end

        function instance.info(message)
            return log_plugin.log('Info', message)
        end

        function instance.warn(message)
            return log_plugin.log('Warn', message)
        end

        function instance.error(message)
            return log_plugin.log('Error', message)
        end

        return instance
    end

    return export
end