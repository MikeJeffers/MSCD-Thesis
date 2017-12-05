## Instructions for Running JAR executable

Install JRE 8.x for your system

Double click the jar to run the game with default settings:  

- Randomly generated map  
- Enabled Neural Network AI  
- No Seeded population  

To run with alternative configurations, pass named arguments by command line: 
 
```
java -jar game.jar --map=MAP --rand=false --seed=false
```


Maps include (shown with matching command line key)

* [BRG]Baton Rouge  
* [CHI]Chicago  
* [COL]Colorado Springs  
* [DEN]Denver  
* [PGH]Pittsburgh  
* [PGH_Closer]Pittsburgh (closer)  
* [SFBAY]San Fransico Bay Area  
* [SJO]San Jose  

Setting ```rand``` to true will init the AI as a random actor.  Actions will be randomly generated.  This was used for benchmarking the learning system.

Setting ```seed``` to true will seed the map with existing population and zoning data as provided by the ```map``` data.  
**Note** this often consumes a lot of heap memory on init.  Allocate more heapspace the the JVM with command line arguments as system defaults tend to be smaller.  
``` java -Xmx1024m ...jar --args```  
Your experience may vary.