function create_extension_factory()
    local internal = _internal
    return function()
        local export = {
            exec = { },
            func = { },
            namespace = { },
            req = { },
            topic = { },
            trigger = { }
        }

        function export.adhoc(cmd)
            local err, res = internal.adhoc({
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
            return err, res
        end

        function export.await(cmd)
            -- FIXME if cmd is string use it directly
            local err, res = internal.await(cmd.req_id)
            return err, res
        end

        function export.await_completed(cmd)
            -- FIXME if cmd is string use it directly
            local err, res = internal.await_completed(cmd.req_id)
            return err, res
        end

        function export.await_failed(cmd)
            -- FIXME if cmd is string use it directly
            local err, res = internal.await_failed(cmd.req_id)
            return err, res
        end

        function export.exec.get(exec_id)
            return internal.get_exec(exec_id)
        end

        function export.exec.list()
            return internal.list_execs()
        end

        function export.func.create(cmd)
            return internal.create_func({
                namespace_id = cmd.namespace_id or nil,
                name = cmd.name or nil,
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
        end

        function export.func.get(func_id)
            return internal.get_func(func_id)
        end

        function export.func.list()
            return internal.list_func()
        end

        function export.namespace.create(cmd)
            return internal.create_namespace({
                name = cmd.name or "",
                inputs = cmd.inputs or {}
            })
        end

        function export.namespace.get(namespace_id)
            return internal.get_namespace(namespace_id)
        end

        function export.namespace.list()
            return internal.list_namespace()
        end

        function export.req.get(req_id)
            return internal.get_req(req_id)
        end

        function export.topic.resolve(topic_name)
            return internal.resolve_topic(topic_name)
        end

        function export.topic.append(topic_id, payload)
            return internal.append_entry(topic_id, payload)
        end

        function export.topic.create(cmd)
            return internal.create_topic({
                name = cmd.name
            })
        end

        function export.topic.list()
            return internal.list_topic()
        end

        function export.topic.list_entries(topic_id)
            return internal.list_topic_entry(topic_id)
        end

        function export.topic.get(topic_id)
            return internal.get_topic(topic_id)
        end

        function export.trigger.create_fixed_rate(cmd)
            return internal.create_trigger({
                type = "FixedRate",
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                duration = cmd.duration
            })
        end

        function export.trigger.create_event(cmd)
            return internal.create_trigger({
                type = "Event",
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                topic_id = cmd.topic_id
            })
        end

        function export.trigger.get(trigger_id)
            return internal.get_trigger(trigger_id)
        end

        function export.trigger.list()
            return internal.list_trigger()
        end

        return export
    end
end