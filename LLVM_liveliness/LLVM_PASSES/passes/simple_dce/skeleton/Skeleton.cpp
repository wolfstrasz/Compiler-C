#define DEBUG_TYPE "SimpleDCEPass"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"
#include "llvm/Transforms/Utils/Local.h"
#include <llvm/Analysis/LoopInfo.h>
#include <vector>
using namespace llvm;



// Pass skeleton
// ------------
namespace {

  struct SimpleDCEPass : public FunctionPass {
    // General data
    static char ID;
    SimpleDCEPass() : FunctionPass(ID) {}
    // Additional data
    std::map<std::string, int> instrCounter;

    // Main functionality
    virtual bool runOnFunction(Function &F) {
     // errs() << "Function  " << F.getName() << "!\n";
      while(eliminateDeadCode(F));
      return true;
    }


    bool eliminateDeadCode (Function &F){
        SmallVector<Instruction*, 64> WorkList;
        for (Function::iterator i = F.begin(), e = F.end(); i != e; i++){
            for (BasicBlock::iterator bbi = i->begin(), bbe = i->end(); bbi != bbe; bbi++ ){
            //     Instruction *instruction = &*bbii;
                if (isInstructionTriviallyDead(&*bbi)){
                    WorkList.push_back(&*bbi);
                }
            }
        }
        // Check if there was no change to exit
        if (WorkList.empty()) return false;
        //errs() << "Found dead vars: " << WorkList.size() << "\n";

        while (!WorkList.empty()){
            WorkList.pop_back_val()->eraseFromParent();
        }
        return true;
    }
};
}

// Registration
// ------------
char SimpleDCEPass::ID = 0;
static
RegisterPass<SimpleDCEPass> X("skeletonpass", "Simple dead code elimination using isInstructionTriviallyDead()");

static void registerSkeletonPass(const PassManagerBuilder &,
                         legacy::PassManagerBase &PM) {
  PM.add(new SimpleDCEPass());
}
static RegisterStandardPasses
  RegisterMyPass(PassManagerBuilder::EP_EarlyAsPossible,
                 registerSkeletonPass);
