function create_capability_factory()
    local internal = _internal
    return function()
        local export = { }

        function export.trace(message)
            -- FIXME message is string
            local err = internal.log('Trace', message)
            return err
        end

        function export.debug(message)
            -- FIXME message is string
            local err = internal.log('Debug', message)
            return err
        end

        function export.info(message)
            -- FIXME message is string
            local err = internal.log('Info', message)
            return err
        end

        function export.warn(message)
            -- FIXME message is string
            local err = internal.log('Warn', message)
            return err
        end

        function export.error(message)
            -- FIXME message is string
            local err = internal.log('Error', message)
            return err
        end

        function export.fatal(message)
            -- FIXME message is string
            local err = internal.log('Fatal', message)
            return err
        end

        return export
    end
end