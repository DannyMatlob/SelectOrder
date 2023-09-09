# SelectOrder
An imitation of SQL

For the coding part of the homework, I want you to show me you still remember Java and to get you thinking about what might be involved in programming even a tiny part of a DBMS system. Two common database queries operations are selection which returns a subset of rows from a database table and order by which sorts the query results in either ascending or descending order according to the values in some column. For this homework, you will write a program SelectOrder.java that will do this for text file based tables. To grade your program, the grader will first compile it using the command:

javac SelectOrder.java
from the command line, and without using any special classpath. So do not have an package statements in your program. You can assume the grader is using some recent variant of Java. Your program will then be run using a line of the form:

java SelectOrder some_file_name WHERE select_col_1.rel_1.value_1 ... select_col_n.rel_n.value_n ORDER_BY sort_column sort_direction
As a concrete example, your program might be run with the line:

java SelectOrder Employee.txt WHERE SALARY.gt.50000 SALARY.lt.100000 MANAGER_ID.eq.51 ORDER_BY LNAME DESC
In terms of command line arguments, some_file_name should be the path to a plain text file in the following format: The first line consists of a sequence of tab delimited column names, subsequent lines consist of tab delimited row data of values for the columns. For example, Employee.txt might look like:

FNAME	LNAME	SALARY	MANAGER_ID
Bob	Smith	750000	54
Sally	Jacob	52000	51
Deepika	Sharma	97534	51
To keep things simple column names and values are strings made of upper case letters, lower case letters, digits 0-9, and underscore. The command line arguments after the WHERE keyword are conditions that a row must satisfy before it is output. For example, SALARY.gt.50000 means that the value in the SALARY column for the row must be greater than 50000. The following are the allowed relations that can be used in conditions: eq (equal), lt (less than), gt (greater than), le (less than or equal), ge (greater than or equal). The first argument of a condition will always be assumed to be a column name and the second argument will always be assumed to be a value the column could take. Values which are made only of digits are compared as integers, values made of letters and digits are compared as strings lexicographically (i.e., based on alphabetical order). For a row to be output all the conditions must be satisfied. Your program should scan the provided file and compute the sequence of rows that would satisfy the query. In a real DB, these results might be streamed to disk before sorting and a disk based sorting algorithm would be used. You can assume for this homework you have enough RAM to keep the selected rows in memory. Your program should then sort these rows according to values in the specified ORDER_BY column and output the sorted rows, one on each line. There are two possible accepted values for sort order that your program should support ASC (ascending order), and DESC (descending order). As an example, if Employee.txt contained the data above, and the command line was as above, then your program should output:

Deepika	Sharma	97534	51
Sally	Jacob	52000	51
If some_file_name is not in the format specified or the command line arguments are not as specified, your program should output:

ERROR!
and stop.

You can assume no single row is more than 32KB. You should not assume that the file some_file_name though is small enough to fit into your computer's RAM memory, however, you can assume the output of any query we will test on can fit in memory. Your program will be tested on large files. This completes the description of the coding part of your homework. Make sure to submit the file SelectOrder.java in the Hw1.zip file you submit.

