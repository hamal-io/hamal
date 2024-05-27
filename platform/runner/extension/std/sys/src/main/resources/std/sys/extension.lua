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
            collection = {},
            exec = { },
            func = { },
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

        return instance
    end

    return export
end