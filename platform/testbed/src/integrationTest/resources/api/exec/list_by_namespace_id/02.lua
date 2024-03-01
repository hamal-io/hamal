sys = require_plugin('sys')

--COLLECT EXECS
execs = fail_on_error(sys.execs.list())

exec_one = find_in_list(execs, 'correlation_id', 'func-1-invoke')
exec_two = find_in_list(execs, 'correlation_id', 'func-2-invoke')

assert(exec_one ~= nil)
assert(exec_two ~= nil)

namespaces = fail_on_error(sys.namespaces.list())
namespace_one = find_in_list(namespaces, 'name', 'root-namespace::namespace-1')
namespace_two = find_in_list(namespaces, 'name', 'root-namespace::namespace-2')

assert(namespace_one ~= nil)
assert(namespace_two ~= nil)

namespace_one_execs = fail_on_error(sys.execs.list({
    namespace_ids = { namespace_one.id }
}))
namespace_two_execs = fail_on_error(sys.execs.list({
    namespace_ids = { namespace_two.id }
}))

assert(namespace_one_execs ~= nil)
assert(namespace_two_execs ~= nil)

--Execs per namespace
assert(#namespace_one_execs == 1)
assert(namespace_one_execs[1].status == 'Completed')
assert(namespace_one_execs[1].id == exec_one.id)

assert(#namespace_two_execs == 1)
assert(namespace_two_execs[1].status == 'Completed')
assert(namespace_two_execs[1].id == exec_two.id)