package io.hamal.module.worker.script.parser

internal enum class Precedence {
    PRECEDENCE_LOWEST,
    PRECEDENCE_ASSIGNMENT,  // =
    PRECEDENCE_EQUALITY,    // == ~=
    PRECEDENCE_COMPARISON,  // < > <= >=
    PRECEDENCE_TERM,        // + -
    PRECEDENCE_FACTOR,      // * /
    PRECEDENCE_PREFIX,       // ~ -
    PRECEDENCE_CALL,        // ()
    PRECEDENCE_HIGHEST
}

