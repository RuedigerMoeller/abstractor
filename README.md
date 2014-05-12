kontraktor
==========

lightweight and efficient Actor/(CSP) implementation in Java. The threading model implemented has many similarities to node.js and go's model of concurrency.

Kontraktor implements a typed actor model to avoid message definition+handling boilerplate code. Additionally this integrates well
with code completion and refactoring of modern IDEs.

[Full Documentation](https://github.com/RuedigerMoeller/kontraktor/wiki/Kontraktor-documentation) is work in progress,

[SampleApp - a nio http 1.0 webserver skeleton done with actors](https://github.com/RuedigerMoeller/kontraktor-samples/tree/master/src/main/java/samples/niohttp)

```xml
<dependency>
    <groupId>de.ruedigermoeller</groupId>
    <artifactId>kontraktor</artifactId>
    <version>LATEST</version>
</dependency>
```

Kontraktor uses runtime-generated (javassist) proxy instances which put all calls to the proxy onto a queue. A DispatcherThread then dequeues method invocations (=messages) ensuring single-threadedness of actor execution.

Kontraktor is work in progress, but 1.0 should be near. It's only 9 classes + some annotations.

E.g.

```java
    public static class BenchSub extends Actor {
        int count;
        
        public void benchCall(String a, String b, String c) {
            count++;
        }
          
        public void getResult( Callback<Integer> cb ) {
            cb.receiveResult(count,null);
        }
    }

    public static main(..) 
    {
        final BenchSub bsProxy = Actors.SpawnActor(BenchSub.class); // create proxy + actor instance
        for (int i : new int[10] ) {
            bsProxy.benchCall("u", "o", null); // actually enqueues
        }
        // all communication is async
        bsProxy.getResult( new Callback<Integer>() {
            @Override
            public void receiveResult(Integer result, Object error) {
                bs.stop();
            }
        });
    }
```

Kontrakor internally uses a high performance bounded queue implementation of the Jaq-In-A-Box project and can pass 
9 million messages per second (on i7 laptop, method with 3 arguments passed) up to 15 million on decent hardware.


