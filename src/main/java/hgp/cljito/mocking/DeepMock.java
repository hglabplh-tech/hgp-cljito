package hgp.cljito.mocking;

import clojure.java.api.Clojure;
import clojure.lang.*;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;

public class DeepMock {


    DeepMock () {

    }

    public static ISeq mockOf(Var... funs) {
        ISeq result = ArraySeq.create();
        for(Var actVar : funs) {
            IFn mockFun = new InternalMockFun(actVar);
            result.cons(mockFun);
            Object rawRoot = actVar.getRawRoot();
            actVar.bindRoot(mockFun);

        }
        return result;
    }


    private static class InternalMockFun extends AFunction {

        private final IFn origFun;

        private final IFn mockFun;

        public InternalMockFun(IFn origFun) {
            this.origFun = origFun;
            this.mockFun = Clojure.var("hgp.cljito.mocking-jay",
                    "fun-mock-call");
        }

        @Override
        public IPersistentMap meta() {
            return super.meta();
        }

        @Override
        public IObj withMeta(IPersistentMap meta) {
            return super.withMeta(meta);
        }

        /* have to look if and how we need invoke also*/
        @Override
        public Object applyTo(ISeq arglist) {
            ISeq newArgs = ArraySeq.create();
            newArgs = newArgs.cons(this.origFun);
            ISeq iterSeq = arglist;
            while (iterSeq.next() != null) {
                newArgs = newArgs.cons(iterSeq.first());
                iterSeq = iterSeq.next();
            }
            return this.mockFun.applyTo(newArgs);
        }

        @Override
        public int compare(Object o1, Object o2) {
            return super.compare(o1, o2);
        }
    }

}
