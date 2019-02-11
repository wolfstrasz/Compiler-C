#include "Test[6] -> Expected: Pass "
#include "Tests: While nesting"

int main (){
    void FIRST_BLOCK;
    void AFTER_WHILE_BLOCK;
    while (a == 1){
        void FIRST_WHILE_BLOCK;

    }
    while (a == 2){
        void SEC_WHILE_BLOCK;
        while (a == 3){
            void NESTED_WHILE;
			{
				{
					// Nested block
				}
			}
        }
    }
}