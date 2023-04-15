package io.hamal.script.ast

interface Node

interface Expression : Node

interface Statement : Node

class ExpressionStatement(val expression: Expression) : Statement

