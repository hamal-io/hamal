function plugin()
    internal = _internal
    return function()
        local export = {
            api = internal.api,
            exec = {
                id = internal.exec_id,
                hook = internal.hook,
                endpoint = internal.endpoint,
                events = internal.events,
            },
            state = internal.state
        }
        function export.complete(result)
            if (type(result) == 'nil') then
                return internal.complete({})
            elseif (type(result) == 'string' or
                    type(result) == 'number' or
                    type(result) == 'boolean') then
                return internal.complete({ value = result })
            else
                return internal.complete(result)
            end
        end

        function export.emit(topic, event)
            if topic == nil then
                return error("Topic not present")
            end
            internal.emit(topic, event or {})
        end

        function export.fail(reason)
            if (type(reason) == 'nil') then
                return internal.fail({})
            elseif type(reason) == 'string' then
                return internal.fail({ message = reason })
            elseif type(reason) == 'number' or type(reason) == 'boolean' then
                return internal.fail({ value = reason })
            else
                return internal.fail(reason)
            end
        end

        return export
    end
end