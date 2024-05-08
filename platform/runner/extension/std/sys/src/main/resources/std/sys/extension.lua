function extension_create()
    local export = { }

    function export.create(cfg)
        cfg = cfg or {}

        exec_namespace_id = context.exec.namespace_id

        local http = require('net.http').create({ base_url = cfg.base_url or 'http://localhost:8008' })

        local instance = {
            exec = { },
            func = { },
            collection = {}
        }

        function instance.exec.list(query)
            query = query or {}
            query.namespace_ids = query.namespace_ids or {}
            query.workspace_ids = query.workspace_ids or {}

            local err, resp = http.get({
                url = '/v1/execs',
                headers = { ['x-exec-token'] = context.exec.token }
            })

            print(context.exec.token)
            print(dump(resp.content))
            -- FIXME handle error

            return err, resp.content.execs
        end

        function instance.func.create(req)
            req = req or {}

            local err, resp = http.post({
                url = '/v1/namespaces/' .. req.namespace_id .. '/funcs',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    name = req.name or nil,
                    inputs = req.inputs or {},
                    code = req.code or "",
                    codeType = req.code_type or "Lua54"
                }
            })
            print(dump(resp.content))
            handle_error(resp, err)
            return err, resp.content
        end

        function instance.func.deploy(req)
            req = req or {}
            local err, resp = http.post({
                url = '/v1/funcs/' .. req.func_id .. '/deploy',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    id = req.id,
                    version = req.version or nil,
                    message = req.message or nil
                }
            })

            handle_error(resp, err)
            return err, resp.content
        end

        function instance.func.deploy_latest(func_id, message)
            req = req or {}
            local err, resp = http.post({
                url = '/v1/funcs/' .. req.func_id .. '/deploy',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    id = req.id,
                    version = nil,
                    message = message or nil
                }
            })

            handle_error(resp, err)
            return err, resp.content
        end

        function instance.func.get(func_id)
            local err, resp = http.get({
                url = '/v1/funcs/' .. func_id,
                headers = { ['x-exec-token'] = context.exec.token }
            })

            handle_error(resp, err)
            return err, resp.content
        end

        function instance.func.list(query)
            query = query or {}

            local err, resp = http.get({
                url = '/v1/funcs',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    namespace_ids = query.namespace_ids or {}
                }
            })

            handle_error(resp, err)
            return err, resp.content.funcs
        end

        function instance.list_deployments(query)
            query = query or {}
            local err, resp = http.get({
                url = '/v1/funcs',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    query.id or nil
                }
            })

            handle_error(resp, err)
            return err, resp.content.deployments
        end

        function instance.func.invoke(func_id, req)
            req = req or {}
            local err, resp = http.post({
                url = '/v1/funcs/' .. func_id .. '/invoke',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    correlationId = req.correlation_id or nil,
                    inputs = req.inputs or {},
                    version = req.version or nil
                }
            })

            handle_error(resp, err)
            return err, resp.content
        end

        function instance.func.update(req)
            req = req or {}
            local err, resp = http.patch({
                url = '/v1/funcs/' .. func_id,
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    name = req.name or nil,
                    inputs = req.inputs or {},
                    code = req.code or nil
                }
            })

            handle_error(resp, err)
            return err, resp.content
        end

        function instance.collection.list(query)
            query = query or { }
            namespace_id = query.namespace_id or exec_namespace_id

            local err, resp = http.get({
                url = '/v1/namespaces/' .. namespace_id .. '/collections',
                headers = { ['x-exec-token'] = context.exec.token }
            })

            return err, resp.content
        end

        function instance.collection.insert_one(cmd)
            cmd = cmd or { }
            namespace_id = cmd.namespace_id or exec_namespace_id
            local err, resp = http.post({
                url = '/v1/namespaces/' .. namespace_id .. '/collections/' .. cmd.collection .. '/insert-one',
                headers = { ['x-exec-token'] = context.exec.token },
                json = { item = cmd.item }
            })

            return err, resp.content
        end

        function instance.collection.find(query)
            query = query or { }
            namespace_id = query.namespace_id or exec_namespace_id

            local err, resp = http.post({
                url = '/v1/namespaces/' .. namespace_id .. '/collections/' .. query.collection .. '/find',
                headers = { ['x-exec-token'] = context.exec.token },
                json = { query = query.query or { } }
            })

            return err, resp.content
        end

        return instance
    end

    return export
end

function handle_error(response, error)
    if response.content['class'] == 'ApiError' then
        print('ApiError message: ' .. response.content['message'])
        if error == nil then
            error = 'ApiError'
        end
    end
    return error
end

