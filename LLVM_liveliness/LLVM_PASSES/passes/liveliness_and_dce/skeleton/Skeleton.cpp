#define DEBUG_TYPE "MyDCEPass"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/InstrTypes.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/ADT/STLExtras.h"
#include "llvm/IR/CFG.h"
#include "llvm/IR/Constants.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/IntrinsicInst.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Type.h"

#include <vector>
#include <algorithm>
#include <set>

using namespace llvm;

namespace
{
struct MyDCEPass : public FunctionPass
{
    static char ID;
    MyDCEPass() : FunctionPass(ID) {}


    std::map<Instruction *, std::set<Instruction *>> succ;
    std::map<Instruction *, std::set<Instruction *>> use;
    std::map<Instruction *, std::set<Instruction *>> def;
    std::map<Instruction *, std::set<Instruction *>> in;
    std::map<Instruction *, std::set<Instruction *>> out;


    std::map<BasicBlock *, std::set<Value*>> blockDef;
    std::map<BasicBlock *, std::set<Value*>> blockUse;


    SmallVector<Instruction*, 64> WorkList;


    // Checks if instruction is dead
    bool isInstructionDead(Instruction* I){
        if (I->isTerminator() || I->mayHaveSideEffects() || isa<PHINode>(I) || isa<CallInst>(I)) return false;
        else {
            return ((out[I].find(I) == out[I].end()));
        }
    }

    /* Eliminates Dead Code */
    bool eliminateDeadCode (Function &F){
        for (Function::iterator i = F.begin(), e = F.end(); i != e; i++){
            for (BasicBlock::iterator bbi = i->begin(), bbe = i->end(); bbi != bbe; bbi++ ){
                if (isInstructionDead(&*bbi)){
                    WorkList.push_back(&*bbi);
                }
            }
        }

        // Check if there was no change to exit
        if (WorkList.empty()) return false;
         while (!WorkList.empty()){
             Instruction* I = WorkList.pop_back_val();
        //     printInstr(I);
             I->eraseFromParent();
         }
        return true;
    }


    virtual bool runOnFunction(Function &F)
    {
        livelinessAnalysis(F);
        printFirstLiveliness(F);

        while(eliminateDeadCode(F)){
            livelinessAnalysis(F);
        }
        return true;
    }

    bool isVar (Value* V){
        return (isa<Instruction>(V) || isa<Argument>(V));
    }

    /*
        Fill in the speacial Use and Def sets of block which are
        associated with PHI nodes
    */
    void createBlockSets(Function &F){
        for (Function::iterator bb = F.begin(), be = F.end(); bb != be; bb++)
        {
            // Get the basic block as pointer
            BasicBlock *B = &*bb;
            for (BasicBlock::iterator ib = B->begin(), ie = B->end(); ib != ie; ib++)
            {
                // Get the instruction as pointer
                Instruction *I = &*ib;
                if (!isa<PHINode>(I))break;
                // go through PHINodes to fill in the sets
                if (PHINode* phiNode = dyn_cast<PHINode>(&*I)){
                    // Add the def
                    blockDef[B].insert(I);
                    // Add the different use sets
                    for (int idx = 0; idx < phiNode->getNumIncomingValues(); idx++) {
                        Value* val = phiNode->getIncomingValue(idx);
                        if (isVar(val)){
                            BasicBlock* incomingBlock = phiNode->getIncomingBlock(idx);
                            blockUse[incomingBlock].insert(val);
                        }

                    }
                }
            }
        }
    }


    /* Fills in the initial sets for all instructions */
    void createInitialSets(Function &F)
    {
        for (Function::iterator bb = F.begin(), be = F.end(); bb != be; bb++)
        {
            BasicBlock *B = &*bb;
            Instruction *T = B->getTerminator();
            for (BasicBlock::iterator ib = B->begin(), ie = B->end(); ib != ie; ib++)
            {
                Instruction *I = &*ib;
                if (isVar(I) && !isa<PHINode>(I)){

                    // GET SUCCESSORS
                    std::set<Instruction *> InstructionSuccessors;
                    if (I == T)
                    {
                        // It is the terminator then it might branch and have != 1 successorIter
                        int nSuccs = T->getNumSuccessors();
                        while (nSuccs != 0)
                        {
                            BasicBlock *SuccBlock = T->getSuccessor(nSuccs - 1);
                            InstructionSuccessors.insert(SuccBlock->getFirstNonPHI());
                            nSuccs --;
                        }
                    }
                    else
                    {
                        // It has only 1 successor
                        Instruction *Succ = &*(std::next(ib));
                        InstructionSuccessors.insert(Succ);
                    }
                    succ[I] = InstructionSuccessors;

                    // GET DEF
                    // BY SSE vars are = to the instruction that declares them
                    std::set<Instruction *> InstrDefinition;
                    InstrDefinition.insert(&*I);
                    def[I] = InstrDefinition;

                    // GET USE
                    // We get the definitions of the vars
                    std::set<Instruction *> UsedVars;
                    int numOperands = I->getNumOperands();

                    while (numOperands > 0)
                    {
                        if (Value *var = dyn_cast<Value>(I->getOperand(numOperands-1)))
                        {
                            if (isVar(var))
                            UsedVars.insert((Instruction*)var);
                        }
                        numOperands--;
                    }
                    use[I] = UsedVars;

                    // GET IN SET
                    std::set<Instruction *> emptyInSet, emptyOutSet;
                    in[I] = emptyInSet;

                    // GET OUT SET
                    out[I] = emptyOutSet;
                }
            }
        }
    }

    /* Performs liveliness analysis */
    void livelinessAnalysis(Function &F)
    {
        clearSets();
        createBlockSets(F);
        createInitialSets(F);
        int i = 0;
        while (1)
        {
            i ++;
            //errs()<< "ITERATION : " << i << "\n";
            bool converged = true;
        //    errs()<<"ANALYSIS-------------------------------------------\n";
            Function::iterator bb = F.end();
            for (Function::iterator be = F.begin(); bb != be;)
            {
                bb --;
            //    printBlock(&*bb);
                BasicBlock *B = &*bb;
                // Backwards data-flow analysis
                for (BasicBlock::reverse_iterator ib = B->rbegin(), ie = B->rend(); ib != ie; ib++)
                {
                    Instruction *I = &*ib;
                    // copy sets
                    std::set<Instruction *> copyIn = in[I];
                    std::set<Instruction *> newIn;
                    std::set<Instruction *> copyOut = out[I];
                    std::set<Instruction *> newOut;

                    if (I == B->getTerminator()){
                        // Calculate terminator out set

                        // ADD [ INx - DEFx ] from PHIs
                        for (auto i=0; i < I->getNumSuccessors(); i++){
                            BasicBlock *Bx = I->getSuccessor(i);
                            // FOR EVERY SUCCESSOR
                            // GET DEF set
                            //auto bxDef = blockDef[Bx];

                            // GET in OF FIRST NON PHI
                            auto bxfNonPhi = Bx->getFirstNonPHI();
                            //auto fNonPhiSet = in[fNonPhi];
                            // and remove DEF of successor from it
                            std::set<Instruction *> result;
                            std::set_difference(in[bxfNonPhi].begin(), in[bxfNonPhi].end(), blockDef[Bx].begin(), blockDef[Bx].end(), std::inserter(result, result.end()));

                            for (auto ib = result.begin(), ie = result.end(); ib != ie; ib++)
                            {
                                newOut.insert((Instruction*)*ib);
                            }
                        }
                        // ADD [USEx] from PHIS
                        for (auto ib = blockUse[B].begin(), ie = blockUse[B].end(); ib!=ie; ib++){
                            newOut.insert((Instruction*)*ib);
                        }

                    } else {// calculate simple out set
                        for (auto node = succ[I].begin(), lastnode = succ[I].end(); node != lastnode; node++)
                        {

                            // for every successor get their IN sets
                            std::set<Instruction *> inSet = in[*node];
                            for (auto ib = inSet.begin(), ie = inSet.end(); ib != ie; ib++)
                            {
                                newOut.insert(*ib);
                            }
                        }
                    }
                    out[I] = newOut;

                    // calculate in set

                    // 1: calculate RES = OUT[n] - DEF[n]
                    // PRINTING CHECK:
                /*    if (isa<ICmpInst> (I)){
                        errs()<< "FOUND CMP INSTR:: "; printInstr(I);
                        printOutSet(I);
                        printDefSet(I);
                        printUseSet(I);
                    }*/
                    std::set<Instruction *> result;
                    std::set_difference(out[I].begin(), out[I].end(), def[I].begin(), def[I].end(), std::inserter(result, result.end()));
                    // 2: calculate USE[n] + RES
                    newIn = use[I];
                    for (auto ib = result.begin(), ie = result.end(); ib != ie; ib++)
                    {
                        newIn.insert(*ib);
                    }
                    // Check PHI incoming nodes
                    // if (isa<PHINode>(*I)){
                    //     int numIncomingNodes = ((PHINode*)I)->getNumIncomingValues();
                    //     errs()<<"NODES: "<< numIncomingNodes << "\n";
                    //     while (numIncomingNodes > 0){
                    //         Instruction* instr = (Instruction*)((PHINode*)I)->getIncomingValue(numIncomingNodes-1);
                    //         newIn.insert(instr);
                    //         numIncomingNodes--;
                    //     }
                    // }
                    in[I] = newIn;
                    // PRINTING CHECK;
                /*    if (isa<ICmpInst> (I)){
                        printInSet(I);
                    }*/
                    // test for convergence
                    if (copyIn != newIn || copyOut != newOut)
                    {
                        converged = false;
                    }
                }
            }

            if (converged)
                break;
        }
    }


    void printFirstLiveliness(Function &F)
    {
        for (Function::iterator bb = F.begin(), be = F.end(); bb != be; bb++)
        {
            BasicBlock *B = &*bb;
            for (BasicBlock::iterator ib = B->begin(), ie = B->end(); ib != ie; ib++)
            {

                Instruction *I = &*ib;
                if (!isa<PHINode>(I))
                    printInSet(I);
            }

        }
        errs()<<"{}\n";
    }


    // UTILITIES:
    // --------------------------------------------------------------------------------

    void clearSets(){
         succ.clear();
         use.clear();
         def.clear();
         in.clear();
         out.clear();
         blockDef.clear();
         blockUse.clear();
    }

    void printBlockSets(Function &F) {
        errs()<<" --------------------------------------------\n";
        errs()<<"                PRINT BLOCK SETS\n";
        errs()<<" --------------------------------------------\n";

        for (Function::iterator bb = F.begin(), be = F.end(); bb != be; bb++)
        {
            BasicBlock *B = &*bb;
            Instruction *I = B->getTerminator();
            printBlock(B);

            // Printing first NON-PHI NODE:
            errs()<<"First non-phi: ";
            printInstr(B->getFirstNonPHI());

            // Printing DEFs from PHIs:
            errs()<<"DEF:\n";
            for (auto xb = blockDef[B].begin(), xe = blockDef[B].end(); xb!=xe; xb++){
                printInstr(((Instruction*)(*xb)));
            }
            // Printing USEs from PHIs:
            errs()<<"\nUSE:\n";
            for (auto xb = blockUse[B].begin(), xe = blockUse[B].end(); xb!=xe; xb++){
                printInstr(((Instruction*)(*xb)));
            }

            // Printing TERMINATOR NODE:
            printInstr (B->getTerminator());
            errs()<<"\n";
        }
    }

    void printInSet(Instruction *I) {
        errs() << "{";
        bool notFirstElem = false;
        for (auto ib = in [I].begin(), ie = in [I].end(); ib != ie; ib++)
        {
            if (notFirstElem) errs()<<",";
            Instruction *instruction = *ib;
            instruction->printAsOperand(errs(), false);
            notFirstElem = true;
        }

        errs() << "}"
               << "\n";
    }

    void printOutSet(Instruction *I) {
        errs() << "{";
        // for (auto ib = out [I].begin(), ie = out [I].end(); ib != ie; ib++)
        // {
        //     Instruction *instruction = *ib;
        //     instruction->printAsOperand(errs(), false);
        // }

        auto ib = out[I].begin();
        Instruction *instruction = *ib;
        instruction->printAsOperand(errs(), false);
        ib++;
        for (auto ie = out[I].end(); ib != ie; ib++)
        {
            errs()<<",";
            instruction = *ib;
            instruction->printAsOperand(errs(), false);

        }
        errs() << "}"
               << "\n";
    }

    void printUseSet(Instruction *I) {
        errs() << "{";
        // for (auto ib = use [I].begin(), ie = use [I].end(); ib != ie; ib++)
        // {
        //     Instruction *instruction = *ib;
        //     instruction->printAsOperand(errs(), false);
        // }
        auto ib = use[I].begin();
        Instruction *instruction = *ib;
        instruction->printAsOperand(errs(), false);
        ib++;
        for (auto ie = use[I].end(); ib != ie; ib++)
        {
            errs()<<",";
            instruction = *ib;
            instruction->printAsOperand(errs(), false);

        }

        errs() << "}"
               << "\n";
    }

    void printDefSet(Instruction *I) {
        errs() << "{";
        // for (auto ib = def [I].begin(), ie = def [I].end(); ib != ie; ib++)
        // {
        //     Instruction *instruction = *ib;
        //     instruction->printAsOperand(errs(), false);
        // }

        auto ib = def[I].begin();
        Instruction *instruction = *ib;
        instruction->printAsOperand(errs(), false);
        ib++;
        for (auto ie = def[I].end(); ib != ie; ib++)
        {
            errs()<<",";
            instruction = *ib;
            instruction->printAsOperand(errs(), false);

        }
        errs() << "}"
               << "\n";
    }

    void printAllBlocks(Function &F) {
        for (Function::iterator bb = F.begin(), be = F.end(); bb != be; bb++)
        {
            printBlock(&*bb);
        }
    }

    void printInstr(Instruction *I) {
        errs() << "------  NEW INSTRUCTION: -------";
        std::string str;
        llvm::raw_string_ostream ss(str);
        ss << *I;
        errs() << ss.str() << "\n";
    }

    void printBlock(BasicBlock *B) {
        errs() << "------  NEW BASIC BLOCK: -------";
        std::string str;
        llvm::raw_string_ostream ss(str);
        ss << *B;
        errs() << ss.str() << "\n";
    }
};

} // namespace

// Registration
// ------------
char MyDCEPass::ID = 0;
__attribute__((unused)) static RegisterPass<MyDCEPass>
    X("live", "my liveliness and dce");
