local function handle_response(err, resp)
    if err ~= nil then
        return err, nil
    end

    if resp.status_code >= 400 then
        if resp.content.class == 'ApiError' then
            print('ApiError: ', resp.content.message)
            err = {
                type = 'ApiError',
                message = resp.content.message
            }
            --error case nil.content would error
            return err, {}
        end
    end

    return nil, resp
end

function extension_create()
    local export = { }

    function export.create(cfg)
        cfg = cfg or {}

        exec_namespace_id = context.exec.namespace_id

        local http = require('net.http').create({ base_url = cfg.base_url or 'http://localhost:8008' })
        local debug = require('std.debug').create()
        local throw = require('std.throw').create()

        local instance = {
            await_completed = {},
            collection = {},
            exec = { },
            func = { },
            namespace = { },
            request = { }
        }

        function instance.await_completed(req)
            req = req or {}
            local count = 0
            repeat
                local status = fail_on_error(instance.request.get(req.requestId))
                status = status.requestStatus
                if status == 'Failed' then
                    throw.internal("Request failed!")
                end
                if count >= 10 then
                    throw.internal("Request Timeout!")
                end

                debug.sleep(10)
                count = count + 1
            until status == 'Completed'
        end

        function instance.exec.get(exec_id)
            local err, resp = handle_response(http.get({
                url = '/v1/execs/' .. exec_id,
                headers = { ['x-exec-token'] = context.exec.token }
            }))

            return err, resp.content
        end

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

        function instance.func.create(req)
            req = req or {}
            namespace_id = req.namespace_id or exec_namespace_id
            local err, resp = handle_response(http.post({
                url = '/v1/namespaces/' .. namespace_id .. '/funcs',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    name = req.name or nil,
                    inputs = req.inputs or {},
                    code = req.code or "",
                    codeType = req.code_type or "Lua54"
                }
            }))

            return err, resp.content
        end

        function instance.func.deploy(req)
            req = req or {}
            local err, resp = handle_response(http.post({
                url = '/v1/funcs/' .. req.id .. '/deploy',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    version = req.version or nil,
                    message = req.message or nil
                }
            }))

            return err, resp.content
        end

        function instance.func.deploy_latest(req)
            req = req or {}
            local err, resp = handle_response(http.post({
                url = '/v1/funcs/' .. req.id .. '/deploy',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    version = nil,
                    message = req.message or nil
                }
            }))

            return err, resp.content
        end

        function instance.func.get(func_id)
            local err, resp = handle_response(http.get({
                url = '/v1/funcs/' .. func_id,
                headers = { ['x-exec-token'] = context.exec.token }
            }))

            return err, resp.content
        end

        function instance.request.get(request_id)
            local err, resp = handle_response(http.get({
                url = '/v1/requests/' .. request_id,
                headers = { ['x-exec-token'] = context.exec.token }
            }))

            return err, resp.content
        end

        function instance.func.invoke(req)
            req = req or {}
            local err, resp = handle_response(http.post({
                url = '/v1/funcs/' .. req.id .. '/invoke',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    correlation_id = req.correlation_id or "__default__",
                    inputs = req.inputs or {},
                    version = req.version or nil
                }
            }))

            return err, resp.content
        end

        function instance.func.list(query)
            query = query or { }
            local url = '/v1/funcs'
            if query.namespace_ids ~= nil then
                local res = ""
                for idx, namespace_id in ipairs(query.namespace_ids) do
                    res = res .. namespace_id
                    if idx < #query.namespace_ids then
                        res = res .. ","
                    end
                end
                url = url .. '?namespace_ids=' .. res
            end

            local err, resp = handle_response(http.get({
                url = url,
                headers = { ['x-exec-token'] = context.exec.token },
            }))

            if resp ~= nil then
                resp = resp.content.funcs
            end

            return err, resp
        end

        function instance.func.list_deployments(query)
            query = query or {}
            local err, resp = handle_response(http.get({
                url = '/v1/funcs/' .. query.id .. '/deployments',
                headers = { ['x-exec-token'] = context.exec.token }
            }))

            return err, resp.content.deployments
        end

        function instance.func.update(req)
            req = req or {}
            local err, resp = handle_response(http.patch({
                url = '/v1/funcs/' .. req.id,
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    name = req.name or nil,
                    inputs = req.inputs or {},
                    code = req.code or nil
                }
            }))

            return err, resp.content
        end

        function instance.namespace.append(req)
            req = req or {}
            local namespace_id = req.namespace_id or exec_namespace_id
            local err, resp = handle_response(http.post({
                url = '/v1/namespaces/' .. namespace_id .. '/namespaces',
                headers = { ['x-exec-token'] = context.exec.token },
                body = {
                    name = req.name,
                    features = nil
                }
            }))

            return err, resp.content
        end

        return instance
    end

    return export
end