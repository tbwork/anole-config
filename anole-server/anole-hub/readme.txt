java -jar anole-hub.jar [parameters]
parameter introduction:
-ba specify the boss server address and port, usage: -ba [ip1]:[port1] [ip2]:[port2] ..., e.g.:
   -ba localhost:22222 localhost:33333
   which starts two bosses at port of 22222 and 33333 respectively. you can also specify it by defining a
   anole property named 
-wn specify the workers' number, usage: -wn [number], e.g.:
   -wn 10
   which will create 10 work processes belongs to the boss server specified by "-ba".
-aw add workers for specified boss servers, usage: -aw [worker's number] [boss' ip] [boss' port] [boss-2' ip] [boss-2' port], e.g.:
   -aw 4 localhost:22222 localhost:33333
   which will create 4 workers for boss "localhost:22222". and the backup boss "localhost:33333"
