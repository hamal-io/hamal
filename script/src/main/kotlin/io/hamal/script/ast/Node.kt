package io.hamal.script.ast

import io.hamal.script.ast.expr.Operator

interface Node



interface Expression : Node




interface Statement : Node

class ExpressionStatement(val expression: Expression) : Statement

