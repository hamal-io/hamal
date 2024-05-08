function plugin_create(internal)
    local export = {
        codes = { },
        endpoints = { },
        execs = { },
        extensions = { },
        funcs = { },
        hooks = { },
        namespaces = { },
        recipes = { },
        reqs = { },
        topics = { },
        triggers = { }
    }

    function export.adhoc(req)
        req = req or {}
        local err, res = internal.adhoc({
            namespace_id = req.namespace_id,
            inputs = req.inputs or {},
            code = req.code or ""
        })
        return err, res
    end

    function export.await(req)
        -- FIXME if req is string use it directly
        internal.await(req.request_id)
    end

    function export.await_completed(req)
        -- FIXME if req is string use it directly
        local err = internal.await_completed(req.request_id)
        if (err ~= nil) then
            error(err.message)
        end
        return err
    end

    function export.await_failed(req)
        -- FIXME if req is string use it directly
        local err = internal.await_failed(req.request_id)
        if (err ~= nil) then
            error(err.message)
        end
        return err
    end

    function export.codes.get(code_id, code_version)
        return internal.code_get(code_id, code_version or -1)
    end

    function export.endpoints.create(req)
        req = req or {}
        return internal.endpoint_create({
            namespace_id = req.namespace_id or nil,
            func_id = req.func_id or nil,
            name = req.name or nil,
            method = req.method or 'Post'
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

    function export.extensions.create(req)
        return internal.extension_create({
            name = req.name or nil,
            code = req.code or ""
        })
    end

    function export.extensions.get(ext_id)
        return internal.extension_get(ext_id)
    end

    function export.extensions.list()
        return internal.extension_list()
    end

    function export.extensions.update(req)
        req = req or {}
        return internal.extension_update(req)
    end

    function export.funcs.create(req)
        req = req or {}
        return internal.func_create({
            namespace_id = req.namespace_id, --FIXME must not be specified for current namespace
            name = req.name or nil,
            inputs = req.inputs or {},
            code = req.code or ""
        })
    end

    function export.funcs.deploy(req)
        req = req or {}
        return internal.func_deploy({
            id = req.id,
            version = req.version or nil,
            message = req.message or nil
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

    function export.hooks.create(req)
        req = req or {}
        return internal.hook_create({
            namespace_id = req.namespace_id or nil,
            name = req.name or nil
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

    function export.namespaces.append(req)
        return internal.namespace_append({
            name = req.name or "",
        })
    end

    function export.namespaces.get(namespace_id)
        return internal.namespace_get(namespace_id)
    end

    function export.namespaces.list()
        return internal.namespace_list()
    end

    function export.namespaces.update(body)
        body = body or {}
        return internal.namespace_update({
            id = body.id,
            name = body.name or nil
        })
    end

    function export.reqs.get(req_id)
        return internal.req_get(req_id)
    end

    function export.recipes.create(req)
        return internal.recipe_create({
            name = req.name or nil,
            inputs = req.inputs or {},
            value = req.value or ""
        })
    end

    function export.recipes.get(recipe_id)
        return internal.recipe_get(recipe_id)
    end

    function export.recipes.update(req)
        return internal.recipe_update(req)
    end

    function export.topics.resolve(topic_name)
        return internal.topic_resolve(topic_name)
    end

    function export.topics.append(topic_id, payload)
        return internal.topic_entry_append(topic_id, payload)
    end

    function export.topics.create(req)
        req = req or {}
        return internal.topic_create({
            namespace_id = req.namespace_id,
            name = req.name
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

    function export.triggers.create_fixed_rate(req)
        req = req or {}
        return internal.trigger_create({
            type = "FixedRate",
            namespace_id = req.namespace_id,
            name = req.name,
            func_id = req.func_id,
            inputs = req.inputs or {},
            duration = req.duration
        })
    end

    function export.triggers.create_event(req)
        req = req or {}
        return internal.trigger_create({
            type = "Event",
            namespace_id = req.namespace_id,
            name = req.name,
            func_id = req.func_id,
            inputs = req.inputs or {},
            topic_id = req.topic_id
        })
    end

    function export.triggers.create_hook(req)
        req = req or {}
        return internal.trigger_create({
            type = "Hook",
            namespace_id = req.namespace_id,
            name = req.name,
            func_id = req.func_id,
            inputs = req.inputs or {},
            hook_id = req.hook_id,
        })
    end

    function export.triggers.create_cron(req)
        req = req or {}
        return internal.trigger_create({
            type = "Cron",
            namespace_id = req.namespace_id,
            name = req.name,
            func_id = req.func_id,
            inputs = req.inputs or {},
            cron = req.cron
        })
    end

    function export.triggers.create_endpoint(req)
        req = req or {}
        return internal.trigger_create({
            type = "Endpoint",
            namespace_id = req.namespace_id,
            name = req.name,
            func_id = req.func_id,
            inputs = req.inputs or {},
            endpoint_id = req.endpoint_id
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

    function export.triggers.activate(req)
        req = req or {}
        return internal.trigger_activate({
            trigger_id = req.id
        })
    end

    function export.triggers.deactivate(req)
        req = req or {}
        return internal.trigger_deactivate({
            trigger_id = req.id
        })
    end

    return export
end