Duplix - Duplicate File Handler
Written by: Bryan R. Martinez
github.com/bryanrm
===============================

Introduction
------------

This program detects duplicate files in a given directory.

It decides a file is a duplicate of another when the SHA-256 hash
of each is equal. By default, the program searches for files recursively
(includes files within inner folders).

Once all duplicates have been found, the appropritate action is taken.
That action depends on the user-entered command-line arguments.


Program Usage
-------------

After downloading the source files in the repository, compile
the files using the command:
javac *.java

Then, run the program using:
java DupliX [source directory] [...optional arguments]

Source directory is required to run the program. The source directory
should be the directory that the user would like to search for duplicates.

java DupliX C:\Users\MyName\Documents\Test
The above command will search the Test directory and all directories 
within for duplicate files and display a list of all duplicate files.

Default behavior is to search all inner directories for duplicate files 
as well. This is called recursive mode, and by default it's turned on.

Modes:
Recursive Mode - searches inner directories for files. Default on.
Save Mode - saves program output to a file. Default off.
            allows user to specify output filename if desired.
Move Mode - moves all duplicate files to a central location. Default off.
            allows user to specify output directory if desired.
Delete Mode - deletes all duplicate files, but keeps a single copy. Default off.


Examples
--------

Run program in recursive mode:
java DupliX C:\Users\MyName\Documents\Test
(it's on by default)

Run program and turn off recursive mode:
java DupliX C:\Users\MyName\Documents\Test -r

Run program, turn off recursive mode, and save output to default file:
java DupliX C:\Users\MyName\Documents\Test -r -s

Run program and save file to specified file and location:
java DupliX C:\Users\MyName\Documents\Test -s C:\Users\Documents\result.txt

Run program and move files to default location:
java DupliX C:\Users\MyName\Documents\Test -m

Run program and move files to specified location:
java DupliX C:\Users\MyName\Documents\Test -m C:\Users\MyName\Documents\Moved

Run program and delete duplicate files:
java DupliX C:\Users\MyName\Documents\Test -d

Run program, export results to default location, and move files to default location:
java DupliX C:\Users\MyName\Documents\Test -s -m
or
java DupliX C:\Users\MyName\Documents\Test -m -s
(order doesn't matter if you're not specifying output directory or output file)
