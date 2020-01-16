# concurrency
Java Concurrency example  

##Fork-Join Pool Examples
1. [FibonacciTask.java](examples/src/main/java/org/sdoroshenko/concurrency/examples/fjp/FibonacciTask.java)
2. [MergeSortTask.java](examples/src/main/java/org/sdoroshenko/concurrency/examples/fjp/MergeSortTask.java)
3. [SizeCalculationTask.java](space-counter/src/main/java/org/sdoroshenko/spacecounter/SizeCalculationTask.java)  

##CompletebleFuture Examples  
CompleteableFuture methods  

| map | reduce | notes |  
|:--- |:--- | ---:|  
| thenApply() |  | in the caller thread |  
| thenAcceptAsync() |  | in the separate thread |  
| thenRun() |  | in the caller thread |  
| thenRunAsync() |  | in the separate thread |  
|  | henCombine() |  |  
|  | allOf() |  |  

thenCompose() => flatMap  
obtrudeValue(T value) => 

27:53
