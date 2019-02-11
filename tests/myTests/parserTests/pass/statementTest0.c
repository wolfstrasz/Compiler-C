#include "lib"


int main() {

    // Singletons
    while(){
        void while1;
    }

    return ;

    // Random block
    {
        void random_block;
    }

    // Nested
    while(){
        while(){
            void nested_while_with_return;
            return;
        }
    }
    // Random nested blocks;
    {
        void parent_block;
        {
            void child_block;
            {
                void more_blocks;
                while(){
                    void and_a_while;
                    return;
                }
            }
            {
                void child_sister;
            }
        }
        while(){
            void while_again;
            return ;
        }
    }

}