# GeanAPI
## A Runtime environment for testing hyper parameter compositions of genetic algorithm 

Remark: This version is a temporary version improved upon in the near future.   

This project implements a runtime environment for a genetic algorithm. With it you can test 
different sets of hyper parameters and adapt it to a new problem instance.  
The example problem that is implemented is the job shop scheduling problem. 
The files implemented for the jssp can be found in `src/main/java/garuntimeenv/gacomponents/jobshop`.

### Installation
The compilation needs at least java 9. The project uses maven. Just call `mvn package` to compile or  
`mvn package -DskipTests` to also skip the tests. The binaries to call are in `target/appassembler/bin/`.


### running
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

### Usage
The current version of this api is the draft version used for my bachelor thesis implementation.
To use it for your own problems beside the JSSP you have to fork it and develop the necessary 
classes indie the given data structure.
Future work will provide this api as a maven repo to include and use comfortably inside your project.

#### To be implemented classes 
The version 1.0.0 requires you to implement the following classes:  
* The problem representation implementing the interface IProblem.
    * It holds the problem with its specific requirements
    * Functionality for decoding or holding a state while decoding
* The solution holding your decoded solution given through ISolution
    * Passed to the fitness function to extract the specific score of the solution 
* The chromosome representation given thorough the IRepresentation Interface  
    * decoding an chromosome into a solution
    * generating new ones 
    * repairing broken chromosomes if necessary
* The used fitness function
    * extracts the specific property needed that should be optimized
