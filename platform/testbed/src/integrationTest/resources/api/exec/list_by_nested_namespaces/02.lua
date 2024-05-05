sys = require_plugin('std.sys')
table = require('std.table').create()

namespaces = fail_on_error(sys.namespaces.list())

root_namespace = table.find(namespaces, 'name', 'root-namespace')
namespace_one = table.find(namespaces, 'name', 'root-namespace::namespace-1')
namespace_two = table.find(namespaces, 'name', 'root-namespace::namespace-2')

root_namespace_execs = fail_on_error(sys.execs.list({
    namespace_ids = { root_namespace.id }
}))
assert(#root_namespace_execs == 4)

namespace_one_execs = fail_on_error(sys.execs.list({
    namespace_ids = { namespace_one.id }
}))
assert(#namespace_one_execs == 1)

namespace_two_execs = fail_on_error(sys.execs.list({
    namespace_ids = { namespace_two.id }
}))
assert(#namespace_two_execs == 1)