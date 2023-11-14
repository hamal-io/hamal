function extension()
    local internal = _internal
    return function()
        local export = {
            blueprints = { },
            codes = { },
            execs = { },
            extensions = { },
            funcs = { },
            hooks = { },
            flows = { },
            reqs = { },
            topics = { },
            triggers = { }
        }

        function export.adhoc(cmd)
            cmd = cmd or {}
            local err, res = internal.adhoc({
                flow_id = cmd.flow_id,
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
            return err, res
        end

        function export.await(cmd)
            -- FIXME if cmd is string use it directly
            local err, res = internal.await(cmd.id)
            return err, res
        end

        function export.await_completed(cmd)
            -- FIXME if cmd is string use it directly
            local err, res = internal.await_completed(cmd.id)
            return err, res
        end

        function export.await_failed(cmd)
            -- FIXME if cmd is string use it directly
            local err, res = internal.await_failed(cmd.id)
            return err, res
        end

        function export.codes.get(code_id, code_version)
            return internal.code_get(code_id, code_version or -1)
        end

        function export.execs.get(exec_id)
            return internal.exec_get(exec_id)
        end

        function export.execs.list(query)
            query = query or {}
            query.flow_ids = query.flow_ids or {}

            return internal.exec_list(query)
        end

        function export.extensions.create(cmd)
            return internal.extension_create({
                name = cmd.name or nil,
                code = cmd.code or ""
            })
        end

        function export.extensions.get(ext_id)
            return internal.extension_get(ext_id)
        end

        function export.extensions.list()
            return internal.extension_list()
        end

        function export.extensions.update(cmd)
            cmd = cmd or {}
            return internal.extension_update(cmd)
        end

        function export.funcs.create(cmd)
            cmd = cmd or {}
            return internal.func_create({
                flow_id = cmd.flow_id,
                name = cmd.name or nil,
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
        end

        function export.funcs.deploy(cmd)
            return internal.func_deploy(cmd)
        end

        function export.funcs.deploy_latest(id)
            return internal.func_deploy_latest(id)
        end

        function export.funcs.get(func_id)
            return internal.func_get(func_id)
        end

        function export.funcs.list(query)
            query = query or {}
            query.flow_ids = query.flow_ids or {}
            return internal.func_list(query)
        end

        function export.funcs.invoke(func_id, body)
            return internal.func_invoke(func_id, body)
        end

        function export.funcs.update(body)
            body = body or {}
            return internal.func_update({
                id = body.id,
                name = body.name or nil,
                inputs = body.inputs or {},
                code = body.code or nil
            })
        end

        function export.hooks.create(cmd)
            cmd = cmd or {}
            return internal.hook_create({
                flow_id = cmd.flow_id or nil,
                name = cmd.name or nil
            })
        end

        function export.hooks.get(hook_id)
            return internal.hook_get(hook_id)
        end

        function export.hooks.list(query)
            query = query or {}
            query.flow_ids = query.flow_ids or {}

            return internal.hook_list(query)
        end

        function export.flows.create(cmd)
            return internal.flow_create({
                name = cmd.name or "",
                inputs = cmd.inputs or {}
            })
        end

        function export.flows.get(flow_id)
            return internal.flow_get(flow_id)
        end

        function export.flows.list()
            return internal.flow_list()
        end

        function export.flows.execs(flow_id)
            return internal.flow_execs(flow_id)
        end

        function export.reqs.get(req_id)
            return internal.req_get(req_id)
        end

        function export.blueprints.create(cmd)
            return internal.blueprint_create({
                name = cmd.name or nil,
                inputs = cmd.inputs or {},
                value = cmd.value or ""
            })
        end

        function export.blueprints.get(blueprint_id)
            return internal.blueprint_get(blueprint_id)
        end

        function export.blueprints.update(cmd)
            return internal.blueprint_update(cmd)
        end

        function export.topics.resolve(topic_name)
            return internal.topic_resolve(topic_name)
        end

        function export.topics.append(topic_id, payload)
            return internal.topic_entry_append(topic_id, payload)
        end

        function export.topics.create(cmd)
            cmd = cmd or {}
            return internal.topic_create({
                flow_id = cmd.flow_id,
                name = cmd.name
            })
        end

        function export.topics.list(query)
            query = query or {}
            query.flow_ids = query.flow_ids or {}

            return internal.topic_list(query)
        end

        function export.topics.list_entries(topic_id)
            return internal.topic_entry_list(topic_id)
        end

        function export.topics.get(topic_id)
            return internal.topic_get(topic_id)
        end

        function export.triggers.create_fixed_rate(cmd)
            cmd = cmd or {}
            return internal.trigger_create({
                type = "FixedRate",
                flow_id = cmd.flow_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                duration = cmd.duration
            })
        end

        function export.triggers.create_event(cmd)
            cmd = cmd or {}
            return internal.trigger_create({
                type = "Event",
                flow_id = cmd.flow_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                topic_id = cmd.topic_id
            })
        end

        function export.triggers.create_hook(cmd)
            cmd = cmd or {}
            return internal.trigger_create({
                type = "Hook",
                flow_id = cmd.flow_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                hook_id = cmd.hook_id,
                hook_methods = cmd.hook_methods or {}
            })
        end

        function export.triggers.get(trigger_id)
            return internal.trigger_get(trigger_id)
        end

        function export.triggers.list(query)
            query = query or {}
            query.flow_ids = query.flow_ids or {}
            return internal.trigger_list(query)
        end

        return export
    end
end