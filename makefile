# Makefile for compiling and running the myrpal Java program
all:
	javac *.java

# This target compiles all Java files in the current directory.
run:
	java myrpal input.txt

# This Makefile compiles all Java files in the current directory and runs the myrpal program with input.txt.
clean:
	del /Q *.class

# Test cases
# Each test case runs a specific test file in the Test directory
test1:
	java myrpal ./Test/test_1
test2:
	java myrpal ./Test/test_2
test3:		
	java myrpal ./Test/test_3					
test4:
	java myrpal ./Test/test_4
test5:
	java myrpal ./Test/test_5
test6:
	java myrpal ./Test/test_6
test7:
	java myrpal ./Test/test_7
test8:
	java myrpal ./Test/test_8
test9:
	java myrpal ./Test/test_9
test10:
	java myrpal ./Test/test_10				
test11:
	java myrpal ./Test/test_11			
test12:
	java myrpal ./Test/test_12
test13:
	java myrpal ./Test/test_13