// COMENTS
//////                      // SKIP
// hgfsdassgdhfhjgkh        // SKIP
// /* sdadsq                // SKIP
/* hgdfgsdfsgdhfh */        // SKIP
/* * sdasad // sadsad */    // SKIP
/* Line 1                   // SKIP
Line 2                      // SKIP
Line 3                      // SKIP
*/ a                        // SKIP, IDENTIFIER("a")

// INCLUDE and errors
#include"ok"
#include "ok"
#include                    // INCLUDE
include                     // IDENTIFIER ("include")
#includ"dsad"               // INVALID
#inclde'f'"sdad"032asd23%   // INVALID
#pragma                     // INVALID
#ifdef                      // INVALID
#define                     // INVALID
#ifndef                     // INVALID
#endif                      // INVALID

// STRING_LITERALS
"String"                    // STRING_LITERAL ("String")
"I am a \"string\""         // STRING_LITERAL ("I am a "string"")
"I am \ta tab "             // STRING_LITERAL ("I am     a tab") 
"I am \ba backspace"        // STRING_LITERAL ("I ama backspace")
"I am \na newline"          // STRING_LITERAL ("I am
                            // a newline")
"I am \ra carriage return??"// a carriage return??)
"I am \fa formfeeder"       // STRING_LITERAL ("I am 
			    //				a formfeeder")
"I am \'"                   // STRING_LITERAL ("I am '")
"I am \""                   // STRING_LITERAL ("I am "")
"I am \\"                   // STRING_LITERAL ("I am \")

// CHAR_LITERALS
'\t'                        // CHAR_LITERAL(	)     		// TAB
'\n'                        // CHAR_LITERAL(
			    // )     				// NEWLINE
'\f'                        // CHAR_LITERAL(
			    // 		    )     		// Formfeed
'\b'                        // CHAR_LITERAL)     		// Backspace
'\r'                        // )HAR_LITERAL(		    	// Carriage Return
'\''                        // CHAR_LITERAL(')            	// Quote
'\"'                        // CHAR_LITERAL(")            	// Double Quote
'a'                         // CHAR_LITERAL(a)            	// a

// NUM_LITERALS
0644253                     // NUM_LITERAL ("0644253")
298                         // NUM_LITERAL ("298")
0                           // NUM_LITERAL ("0")

// IDENTIFIERS
identifier                  // IDENTIFIER ("identifier")
sdaj7                       // IDENTIFIER ("sdaj7")
wjadu765dsad6sa5dsa6ds8ad6  // IDENTIFIER ("wjadu765dsad6sa5dsa6ds8ad6")
intint                      // IDENTIFIER ("intint")
structa                     // IDENTIFIER ("structa")
while1                      // IDENTIFIER ("while1")
returnA                     // IDENTIFIER ("returnA")
nstruct                     // IDENTIFIER ("nstruct")
new                         // IDENTIFIER ("new")
long                        // IDENTIFIER ("long")
string                      // IDENTIFIER ("string")
String                      // IDENTIFIER ("String")
short                       // IDENTIFIER ("short")
unsigned                    // IDENTIFIER ("unsigned")
class                       // IDENTIFIER ("class")
main                        // IDENTIFIER ("main")
_iden                       // IDENTIFIER ("_iden")
iden_                       // IDENTIFIER ("iden_")
_2i_23d                     // IDENTIFIER ("_2i_23d")
2i_23d_                     // INT_LIT("2") IDENTIFIER ("i_23d_")

// TYPES
int                         // INT
void                        // VOID
char                        // CHAR

// KEYWORDS
if                          // IF
else                        // ELSE
while                       // WHILE
return                      // RETURN
struct                      // STRUCT
sizeof                      // SIZEOF

// DELIMITERS
    {                       // LBRA
}                           // RBRA
  (                         // LPAR
    )                       // RPAR
[                           // LSBR
]                           // RSBR
;                           // SC
,                           // COMMA

// OPERATORS
+                           // PLUS
-                           // MINUS
*                           // ASTERIX
/                           // DIV
%                           // REM
.                           // DOT
&&                          // AND
||                          // OR

// ASSIGN and COMPARISONS
=                           // ASSIGN
==                          // EQ
===                         // EQ, ASSIGN
!=                          // NE
<                           // LT
<=                          // LE
<>                          // LT GT
>=                          // GE
>                           // GT



// MIXES
213Asdas123                 // NUM_LITERAL ("213"), IDENTIFIER ("Asdas123")
das%70                      // IDENTIFIER ("das"), REM, NUM_LITERAL("70")
das&&r                      // IDENTIFIER ("das"), AND, IDENTIFIER("r")

// INVALIDS
!                           // INVALID
&                           // INVALID
|                           // INVALID
@                           // INVALID
~                           // INVALID
`                           // INVALID
¬                           // INVALID
^                           // INVALID
?                           // INVALID
:                           // INVALID
$                           // INVALID
£                           // INVALID
'aa'                        // INVALID
'                           // INVALID
''                          // INVALID
'''                         // INVALID, INVALID
' ' '			    // CHAR_LIT( ), INVALID

/**                         // INVALID






