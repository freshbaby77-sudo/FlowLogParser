# FlowLogParser

## Input

The arguments should only includes the path of:
1. Flow log plain text file

The format of flow log file should be similar to this:

2 123456789010 eni-1235b8ca123456789 172.31.16.139 172.31.16.21 20641 22 6 20 4249 1418530010 1418530070 ACCEPT OK
2 123456789010 eni-1235b8ca123456789 172.31.9.69 172.31.9.12 49761 3389 6 20 4249 1418530010 1418530070 REJECT OK

The column should be delimited by comma, space or tab. Please refer to this link(https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html) for details

2. tag map lookup table plain text file

The format of lookup table file should be similar to this:

25,tcp,sv_P1  
68,udp,sv_P2   
23,tcp,sv_P1 x 
31,udp,SV_P3 
443,tcp,sv_P2   

## Usage

Compile
javac FlowLogParser.java

Run
java FlowLogParser [flow log file path] [look up table file path]

It should generate the tagCount.txt and combinationCount.txt accordingly

tagCount.txt

Tag.             Count 

Untagged    2 

 sv_P2          2 

 SV_P3         1 

 sv_P1          2

combinationCount.txt

 Port.   Protocol. Count 

23.     tcp       1 

80      tcp       1 

68      udp      1 

25      tcp       1 

31      udp      1  

443.  tcp       1

## Test

Tested already with sample-flow-logs.txt (larger than 10MB) and lookup_table.txt(more than 10k lines) and it works well.

javac FlowLogParser.java

java FlowLogParser  sample-flow-logs.txt lookup_table.txt

## Analytics

The parser class assume follows the standard format of example input above and no empty or blank lines or lines with wrong format

The parser class assume you compile and run the file in latest JDK environment

The parser class enable BufferredReader to iterate the log file line by line to update the map efficiently to save memory instead of storing them all in advance 

The parser class use nested map to store cominbation and lookup tag map to save storage.

