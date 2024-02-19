function plugin()
    local internal = _internal
    return function()
        local export = {
            blueprints = { },
            codes = { },
            endpoints = { },
            execs = { },
            extensions = { },
            funcs = { },
            hooks = { },
            namespaces = { },
            reqs = { },
            topics = { },
            triggers = { }
        }

        function export.adhoc(cmd)
            cmd = cmd or {}
            local err, res = internal.adhoc({
                namespace_id = cmd.namespace_id,
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
            return err, res
        end

        function export.await(cmd)
            -- FIXME if cmd is string use it directly
            internal.await(cmd.id)
        end

        function export.await_completed(cmd)
            -- FIXME if cmd is string use it directly
            local err = internal.await_completed(cmd.id)
            if (err ~= nil) then
                error(err.message)
            end
            return err
        end

        function export.await_failed(cmd)
            -- FIXME if cmd is string use it directly
            local err = internal.await_failed(cmd.id)
            if (err ~= nil) then
                error(err.message)
            end
            return err
        end

        function export.codes.get(code_id, code_version)
            return internal.code_get(code_id, code_version or -1)
        end

        function export.endpoints.create(cmd)
            cmd = cmd or {}
            return internal.endpoint_create({
                namespace_id = cmd.namespace_id or nil,
                func_id = cmd.func_id or nil,
                name = cmd.name or nil,
                method = cmd.method or 'Post'
            })
        end

        function export.endpoints.get(endpoint_id)
            return internal.endpoint_get(endpoint_id)
        end

        function export.endpoints.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}
            query.workspace_ids = query.workspace_ids or {}

            return internal.endpoint_list(query)
        end

        function export.execs.get(exec_id)
            return internal.exec_get(exec_id)
        end

        function export.execs.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}
            query.workspace_ids = query.workspace_ids or {}

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
                namespace_id = cmd.namespace_id,
                name = cmd.name or nil,
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
        end

        function export.funcs.deploy(cmd)
            cmd = cmd or {}
            return internal.func_deploy({
                id = cmd.id,
                version = cmd.version or nil,
                message = cmd.message or nil
            })
        end

        function export.funcs.deploy_latest(id, message)
            return internal.func_deploy({
                id = id,
                version = nil,
                message = message or nil
            })
        end

        function export.funcs.list_deployments(query)
            query = query or {}
            return internal.func_deployment_list(
                    query.id or nil
            )
        end

        function export.funcs.get(func_id)
            return internal.func_get(func_id)
        end

        function export.funcs.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}
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
                namespace_id = cmd.namespace_id or nil,
                name = cmd.name or nil
            })
        end

        function export.hooks.get(hook_id)
            return internal.hook_get(hook_id)
        end

        function export.hooks.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}

            return internal.hook_list(query)
        end

        function export.namespaces.append(cmd)
            return internal.namespace_append({
                name = cmd.name or "",
            })
        end

        function export.namespaces.get(namespace_id)
            return internal.namespace_get(namespace_id)
        end

        function export.namespaces.list()
            return internal.namespace_list()
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
                namespace_id = cmd.namespace_id,
                name = cmd.name
            })
        end

        function export.topics.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}

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
                namespace_id = cmd.namespace_id,
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
                namespace_id = cmd.namespace_id,
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
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                hook_id = cmd.hook_id,
                hook_method = cmd.hook_method
            })
        end

        function export.triggers.create_cron(cmd)
            cmd = cmd or {}
            return internal.trigger_create({
                type = "Cron",
                namespace_id = cmd.namespace_id,
                name = cmd.name,
                func_id = cmd.func_id,
                inputs = cmd.inputs or {},
                cron = cmd.cron
            })
        end

        function export.triggers.get(trigger_id)
            return internal.trigger_get(trigger_id)
        end

        function export.triggers.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}
            return internal.trigger_list(query)
        end

        function export.triggers.activate(cmd)
            cmd = cmd or {}
            return internal.trigger_activate({
                trigger_id = cmd.id
            })
        end

        function export.triggers.deactivate(cmd)
            cmd = cmd or {}
            return internal.trigger_deactivate({
                trigger_id = cmd.id
            })
        end

        return export
    end
end