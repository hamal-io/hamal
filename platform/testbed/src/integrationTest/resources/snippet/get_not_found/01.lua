sys = require('sys')
--
err, snippet = sys.snippet.get('123123123')
assert(err.message == 'Snippet not found')
assert(err['message'] == 'Snippet not found')
assert(snippet == nil)