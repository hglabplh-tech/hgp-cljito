package hgp.cljito;

import java.util.concurrent.atomic.AtomicReference;
import clojure.lang.*;


public class DeepMock {

    private final Atom bindings;
    private final Atom oldBindings;
    private DeepMock() {
        this.bindings = new Atom(new AtomicReference<IPersistentMap>());
        this.oldBindings = new Atom(new AtomicReference<IPersistentMap>());
    }

    public static IPersistentMap createDeepMockFor(IFn... funs) {
        DeepMock inst = new DeepMock();
        return inst.mockOf(funs);
    }

    public IPersistentMap mockOf(IFn... funs) {// change to map
        IPersistentMap result = PersistentArrayMap.EMPTY;
        IPersistentMap oldVals = PersistentArrayMap.EMPTY;
        for(Var actVar : funs) {
            Symbol mockGenFunSym = Symbol.create("hgp.cljito.mocking-jay",
                    "mock-call");
            IFn mockGenFun = Var.find(mockGenFunSym);
            ISeq funArgList = ArraySeq.create(actVar);
            IFn mockFun = (IFn) mockGenFun.applyTo(funArgList);
            Object rawRoot = actVar.getRawRoot(); // store this look ad redef macro
            oldVals =  oldVals.assoc(actVar, rawRoot);
            actVar.bindRoot(mockFun);
            result = result.assoc(actVar, mockFun);
        }
        IPersistentCollection col =  RT.conj(result, this.bindings.deref());
        this.bindings.reset(col);
        col =  RT.conj(result, this.oldBindings.deref());
        this.oldBindings.reset(col);
        return result;
    }


}
