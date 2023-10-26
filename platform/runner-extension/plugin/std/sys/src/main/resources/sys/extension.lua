function extension()
    local internal = _internal
    return function()
        local export = {
            code = { },
            exec = { },
            extension = { },
            func = { },
            hook = { },
            namespace = { },
            req = { },
            snippet = { },
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

        function export.code.get(code_id, code_version)
            return internal.code_get(code_id, code_version or -1)
        end

        function export.exec.get(exec_id)
            return internal.exec_get(exec_id)
        end

        function export.exec.list()
            return internal.exec_list()
        end

        function export.extension.create(cmd)
            return internal.extension_create({
                name = cmd.name or nil,
                code = cmd.code or ""
            })
        end

        function export.extension.get(ext_id)
            return internal.extension_get(ext_id)
        end

        function export.extension.list()
            return internal.extension_list()
        end

        function export.extension.update(ext_id, cmd)
            return internal.extension_update(ext_id, cmd)
        end

        function export.func.create(cmd)
            return internal.func_create({
                namespace_id = cmd.namespace_id or nil,
                name = cmd.name or nil,
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
        end

        function export.func.get(func_id)
            return internal.func_get(func_id)
        end

        function export.func.list()
            return internal.func_list()
        end

        function export.func.invoke(func_id, body)
            return internal.func_invoke(func_id, body)
        end

        function export.hook.create(cmd)
            return internal.hook_create({
                namespace_id = cmd.namespace_id or nil,
                name = cmd.name or nil
            })
        end

        function export.hook.get(hook_id)
            return internal.hook_get(hook_id)
        end

        function export.hook.list()
            return internal.hook_list()
        end

        function export.namespace.create(cmd)
            return internal.namespace_create({
                name = cmd.name or "",
                inputs = cmd.inputs or {}
            })
        end

        function export.namespace.get(namespace_id)
            return internal.namespace_get(namespace_id)
        end

        function export.namespace.list()
            return internal.namespace_list()
        end

        function export.req.get(req_id)
            return internal.req_get(req_id)
        end

        function export.snippet.create(cmd)
             return internal.snippet_create({
                name = cmd.name or nil,
                inputs = cmd.inputs or {},
                value = cmd.value or ""
             })
        end

        function export.snippet.get(snippet_id)
            return internal.snippet_get(snippet_id)
        end

        function export.snippet.update(snippet_id, cmd)
            return internal.snippet_update(snippet_id, cmd)
        end

        function export.topic.resolve(topic_name)
            return internal.topic_resolve(topic_name)
        end

        function export.topic.append(topic_id, payload)
            return internal.topic_entry_append(topic_id, payload)
        end

        function export.topic.create(cmd)
            return internal.topic_create({
                name = cmd.name
            })
        end

        function export.topic.list()
            return internal.topic_list()
        end

        function export.topic.list_entries(topic_id)
            return internal.topic_entry_list(topic_id)
        end

        function export.topic.get(topic_id)
            return internal.topic_get(topic_id)
        end

        function export.trigger.create_fixed_rate(cmd)
            return internal.trigger_create({
                type = "FixedRate",
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                duration = cmd.duration
            })
        end

        function export.trigger.create_event(cmd)
            return internal.trigger_create({
                type = "Event",
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                topic_id = cmd.topic_id
            })
        end

        function export.trigger.create_hook(cmd)
            return internal.trigger_create({
                type = "Hook",
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                hook_id = cmd.hook_id,
                hook_methods = cmd.hook_methods or {}
            })
        end

        function export.trigger.get(trigger_id)
            return internal.trigger_get(trigger_id)
        end

        function export.trigger.list()
            return internal.trigger_list()
        end

        return export
    end
end