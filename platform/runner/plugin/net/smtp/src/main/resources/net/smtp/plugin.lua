local function require_boolean(tbl, key)
    local value = tbl[key]
    if value == nil then
        error(key .. ' not set')
    end

    if type(value) ~= 'boolean' then
        error(key .. ' expected to be of type boolean but was ' .. type(value))
    end

    return value
end

local function require_number(tbl, key)
    local value = tbl[key]
    if value == nil then
        error(key .. ' not set')
    end
    if type(value) ~= 'number' then
        error(key .. ' expected to be of type number but was ' .. type(value))
    end

    return value
end

local function require_string(tbl, key)
    local value = tbl[key]
    if value == nil then
        error(key .. ' not set')
    end
    if type(value) ~= 'string' then
        error(key .. ' expected to be of type string but was ' .. type(value))
    end

    return value
end

local function maybe_string(tbl, key)
    local value = tbl[key]
    if value == nil then
        return nil
    end
    if type(value) ~= 'string' then
        error(key .. ' expected to be of type string but was ' .. type(value))
    end

    return value
end

function plugin_create(intenral)
    local export = { }

    function export.send(_mail)
        local mail = _mail or {}

        internal.send({
            default_encoding = require_string(mail, 'default_encoding'),
            host = require_string(mail, 'host'),
            port = require_number(mail, 'port'),
            username = maybe_string(mail, 'username'),
            password = maybe_string(mail, 'password'),
            protocol = require_string(mail, 'protocol'),
            debug = require_boolean(mail, 'debug'),
            enable_starttls = require_boolean(mail, 'enable_starttls'),
            test_connection = require_boolean(mail, "test_connection"),

            from = require_string(mail, 'from'),
            to = require_string(mail, 'to'),
            subject = require_string(mail, 'subject'),
            content = require_string(mail, 'content'),
            content_type = require_string(mail, 'content_type'),
            priority = require_number(mail, 'priority'),

            connection_timeout = require_number(mail, "connection_timeout"),
            timeout = require_number(mail, "timeout"),
            write_timeout = require_number(mail, "write_timeout")
        })

    end

    return export
end