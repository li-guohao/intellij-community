// "Cast expression 'module' to 'LinkedHashSet<Int>'" "true"
// DISABLE-ERRORS

fun foo(): java.util.LinkedHashSet<Int> {
    val module: java.util.HashSet<Int> = java.util.LinkedHashSet<Int>()
    return module<caret>
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.CastExpressionFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KotlinApplicatorBasedQuickFix