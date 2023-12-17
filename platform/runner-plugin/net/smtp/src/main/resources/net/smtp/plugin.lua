local function require_boolean(mail, key, default)
    local value = mail[key] or default
    if value == nil then
        error(key .. ' not set')
    end

    if type(value) ~= 'boolean' then
        error(key .. ' expected to be of type boolean but was ' .. type(value))
    end

    return value
end

local function require_number(mail, key, default)
    local value = mail[key] or default
    if value == nil then
        error(key .. ' not set')
    end
    if type(value) ~= 'number' then
        error(key .. ' expected to be of type number but was ' .. type(value))
    end

    return value
end

local function require_string(mail, key, default)
    local value = mail[key] or default
    if value == nil then
        error(key .. ' not set')
    end
    if type(value) ~= 'string' then
        error(key .. ' expected to be of type string but was ' .. type(value))
    end

    return value
end

local function maybe_string(mail, key)
    local value = mail[key]
    if value == nil then
        return nil
    end
    if type(value) ~= 'string' then
        error(key .. ' expected to be of type string but was ' .. type(value))
    end

    return value
end

function plugin()
    local internal = _internal
    return function()
        local export = { }

        function export.send(_mail)
            local mail = _mail or {}

            internal.send({
                default_encoding = require_string(mail, 'default_encoding', 'UTF-8'),
                host = require_string(mail, 'host', nil),
                port = require_number(mail, 'port', 25),
                username = maybe_string(mail, 'username'),
                password = maybe_string(mail, 'password'),
                protocol = require_string(mail, 'protocol', 'smtp'),
                debug = require_boolean(mail, 'debug', false),
                enable_starttls = require_boolean(mail, 'enable_starttls', false),
                test_connection = require_boolean(mail, "test_connection", false),

                from = require_string(mail, 'from', nil),
                to = require_string(mail, 'to', nil),
                subject = require_string(mail, 'subject', nil),
                content = require_string(mail, 'content', nil),
                content_type = require_string(mail, 'content_type', 'text/plain'),
                priority = require_number(mail, 'priority', 1),

                connection_timeout = require_number(mail, "connection_timeout", 5000),
                timeout = require_number(mail, "timeout", 5000),
                write_timeout = require_number(mail, "write_timeout", 3000)
            })

        end

        return export
    end
end