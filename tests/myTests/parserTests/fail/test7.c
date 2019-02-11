#include "Test[7] -> Expected: Fail"
#include "Tests: nested struct decl"

struct _parent {
    void parent;
    struct _child{
        void child;
    };
};