local function handle_response(err, resp)
    if err ~= nil then
        return err, nil
    end

    if resp.status_code >= 400 then
        if resp.content['class'] == 'ApiError' then
            print('ApiError: ' .. resp.content['message'])
            err = {
                type = 'ApiError',
                message = resp.content['message']
            }
            return err, nil
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

        local instance = {
            exec = { },
            func = { },
            collection = {}
        }

        function instance.await_completed(req)
            -- FIXME pull requests from backend
            req = req or {}

            print(dump(req))

            local count = 0
            while req.requestStatus ~= 'Completed' do
                if req.requestStatus == 'Failed' then
                    error("Request failed!")
                end
                if count >= 10 then
                    error("Request Timeout")
                end
                debug.sleep(1000)
                count = count + 1
            end

            return true
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
                    codeType = req.code_type or "Lua54" --FIXME-341 register snakecase somewhere
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

        return instance
    end

    return export
end