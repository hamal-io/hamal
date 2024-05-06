
function extension_create()
    local throw = require('std.throw').create()
    local smtp = require_plugin('net.smtp')

    local export = { }

    local function require_boolean(tbl, key, default)
        local value = tbl[key] or default
        if value == nil then
            throw.illegal_argument(key .. ' not set')
        end

        if type(value) ~= 'boolean' then
            throw.illegal_argument(key .. ' expected to be of type boolean but was ' .. type(value))
        end

        return value
    end

    local function require_number(tbl, key, default)
        local value = tbl[key] or default
        if value == nil then
            throw.illegal_argument(key .. ' not set')
        end
        if type(value) ~= 'number' then
            throw.illegal_argument(key .. ' expected to be of type number but was ' .. type(value))
        end

        return value
    end

    local function require_string(tbl, key, default)
        local value = tbl[key] or default
        if value == nil then
            throw.illegal_argument(key .. ' not set')
        end
        if type(value) ~= 'string' then
            throw.illegal_argument(key .. ' expected to be of type string but was ' .. type(value))
        end

        return value
    end

    local function maybe_string(tbl, key)
        local value = tbl[key]
        if value == nil then
            return nil
        end
        if type(value) ~= 'string' then
            throw.illegal_argument(key .. ' expected to be of type string but was ' .. type(value))
        end

        return value
    end

    function export.create(_cfg)
        local instance = {
            cfg = {
                default_encoding = require_string(_cfg, 'default_encoding', 'UTF-8'),
                host = require_string(_cfg, 'host', nil),
                port = require_number(_cfg, 'port', 25),
                username = maybe_string(_cfg, 'username'),
                password = maybe_string(_cfg, 'password'),
                protocol = require_string(_cfg, 'protocol', 'smtp'),
                debug = require_boolean(_cfg, 'debug', false),
                enable_starttls = require_boolean(_cfg, 'enable_starttls', false),
                test_connection = require_boolean(_cfg, "test_connection", false),

                connection_timeout = require_number(_cfg, "connection_timeout", 5000),
                timeout = require_number(_cfg, "timeout", 5000),
                write_timeout = require_number(_cfg, "write_timeout", 3000)
            }
        }

        function instance.send(_mail)
            local mail = _mail or {}

            smtp.send({
                default_encoding = instance.cfg.default_encoding,
                host = instance.cfg.host,
                port = instance.cfg.port,
                username = instance.cfg.username,
                password = instance.cfg.password,
                protocol = instance.cfg.protocol,
                debug = instance.cfg.debug,
                enable_starttls = instance.cfg.enable_starttls,
                test_connection = instance.cfg.test_connection,

                from = require_string(mail, 'from', nil),
                to = require_string(mail, 'to', nil),
                subject = require_string(mail, 'subject', nil),
                content = require_string(mail, 'content', nil),
                content_type = require_string(mail, 'content_type', 'text/plain'),
                priority = require_number(mail, 'priority', 1),

                connection_timeout = instance.cfg.connection_timeout,
                timeout = instance.cfg.timeout,
                write_timeout = instance.cfg.write_timeout
            })
        end

        return instance
    end
    return export
end