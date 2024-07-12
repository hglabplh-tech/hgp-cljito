# cljito/fun - functional mocking vs. Mockito
cljito is a Mockito wrapper for Clojure. Unlike the other libraries,
cljito aims to be a super-thin wrapper over Mockito, so that
cljito can (hopefully) support Mockito's bells and whistles
with as little changes as possible.

**cljito/fun - __!!this fork!!__ - is a mocking on base of real functional
programming in clojure using the language features as defmacro and with-redefs
for mocking the functions also the meta information of the clojure compiler is used**


## Usage

In your `project.clj`, add the dev dependencies 

and you are all set to start using fun/mocking in your tests.

The base of this kind of mocking is that you can define rules and dependent on the rules either
the real function is called or that a action defined in `when->` takes place:

```clojure
(when-> i-am-a-fake-fun return-val [5]
        any-int? any-int? any-int?)
```
#### **Short explanation of `when->` :**
- the first parameter is the name of the **__existing__** function. 
- the second parameter is the action taken if the function is __'mocked'__
- the third parameter are the argument(s) for the action function
- the __**rest**__ of the parameters are the condition functions one for each parameter
if the parameter count will differ from the count of attributes given
it will lead to an execution error.


Here is the simple call with which the function is called but the difference is that 
the fact if the function is called or a **__mock__** of it depends on a `when->`statement
like the when statement above 

```clojure
(fun-mock-call i-am-a-fake-fun 7 8 9)
```

The next way of mocking is the macro `fun-mock` with this macro a block with one or
more functions being **__'mocked'__**:

Here a example call 
```clojure
(fun-mock [i-am-a-fake] [12 9 3]
                        (fn [& arguments]
                          (println arguments)))))
```
The code snippet above demonstrates that very little is
cljito specific; `.thenReturn` and `.thenThrow` are really Mockito
methods. Despite that, cljito also provides helper functions (e.g.,
`at-least`, `at-least`, `never`) to make calls to Mockito's static
methods easier.



The original cljito works with:

1. Mockito 1.9.5.
2. Mockito 2.25.0.
3. Mockito 3.3.0.

## License

Copyright (c) 2020 Shaolang / Copyright (c) 2024 Glab-Plhak

Distributed under the Eclipse Public License, the same as Clojure.
