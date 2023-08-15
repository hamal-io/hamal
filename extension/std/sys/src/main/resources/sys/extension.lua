function create_extension_factory()
    local internal = _internal
    return function()
        local export = {
            exec = { },
            func = { },
            namespace = { },
            req = { }
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

        return export
    end
end