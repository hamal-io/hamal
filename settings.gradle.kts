rootProject.name = "hamal"

include("application")

include("lib")

include(":lib:ddd")
include(":lib:domain")
include(":lib:domain-value-object")
include(":lib:meta")
include(":lib:util")

include(":module")
include(":module:launchpad")
include(":module:launchpad:application")
include(":module:launchpad:core")
include(":module:launchpad:infra")