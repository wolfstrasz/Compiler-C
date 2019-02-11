# MiniC Compiler with liveliness analysis LLVM pass
A compiler that can handle all main functionalities of a C program. Can be tested in 4 states:
* Lexing
* Parsing
* AST and Typechecking
* Code generation in Assembly.

## MiniC language
The grammar and typing rules are provided in ./grammar/

## Building and testing
# Build
''' 
> ant build
'''

# Running
Can be done in 5 different: 
-lexer (lexer output)
-parser (parser output)
-ast (Abstract Syntax Tree output)
-sem (Semantics analysis and typechecking)
-gen (code generations)
'''
> java -cp bin Main [FUNCTIONALITY] [PATH_TO_TEST] [PATH_TO_OUT_FILE]
'''

## Example code generation of fibonacci test
'''
>> java -cp bin Main -gen tests/fibonacci.c fibonacci.asm
'''

## Tests
All tests can be found in test folder. All of them successfully compile and run.

# LLVM Liveliness analysis with dead code elimination (DCE)
Can be found in LLVM folder.
