
# Runtime environment for testing hyper parameter compositions of genetic algorithm 

This project implements a runtime environment for a genetic algorithm. With it you can test 
different sets of hyper parameters and adapt it to a new problem instance.  
The example problem that is implemented is the job shop scheduling problem. 
The files implemented for the jssp can be found in `src/main/java/garuntimeenv/gacomponents/jobshop`.

### Installation
The compilation needs at least java 9. The project uses maven. Just call `mvn package` to compile or  
`mvn package -DskipTests` to also skip the tests. The binaries to call are in `target/appassembler/bin/`.


### Usage
```
usage: ./target/appassembler/bin/garuntimeenv
 -?,--help             For help
 -i,--instance <arg>   the respective instance in the json file if non is
                       given a random one will be picked
 -p,--problem <arg>    The problem name [Job_Shop_Scheduling]
 -r,--run <arg>        Set true if the runtime environment should only run
                       to test hyper parameter's set false
 -sp,--savePictures    if flag is set saves the pictures from the UI if
                       enabled
 -v,--visual           if flag is set enable visual
 -w,--writeData        If no data shall be written.
 ```

To run the application with the 15 x 15 job shop scheduling instance and visual enable call: 
```
./target/appassembler/bin/garuntimeenv -r true -p Job_Shop_Scheduling -v -i 1
```

To run the genetic algorithm with the default configuration call
```
./target/appassembler/bin/garuntimeenv -r false -p Job_Shop_Scheduling -v -i 1
``` 