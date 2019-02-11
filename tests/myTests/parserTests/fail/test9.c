#include "Test[9] -> Expected: Fail"
#include "Tests: Var decl after stmt decl in function"

int _function(){
    void _var_decl;
    while(){
        _while_statement;
    }
    void _after_statement_var_decl;
}