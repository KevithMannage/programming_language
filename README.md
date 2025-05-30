# RPAL Programming Project - CS 3513 Programming Language

## Group Synalyze

**Group Members**:  
- Mannage K.M.K.K (220384B)  
- Jayakody J.A.U.C (220248M)

## Project Description

This project implements a complete interpreter for the **RPAL (Right-reference Pascal)** programming language as part of the CS 3513 Programming Language course. The interpreter comprises four key components:

1. **Lexical Analyzer**: Tokenizes the input source code based on the lexical rules defined in [RPAL_Lex.pdf](Docs/RPAL_Lex.pdf).
2. **Parser**: Constructs an Abstract Syntax Tree (AST) from the token stream using the grammar rules provided in [RPAL_Grammar.pdf](Docs/RPAL_Grammar.pdf).
3. **AST Standardizer**: Transforms the AST into a Standardized Tree (ST) by eliminating syntactic sugar and normalizing expressions for evaluation.
4. **CSE Machine (Control Stack Environment Machine)**: Evaluates the ST using functional evaluation semantics to produce the final program output.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure Java is installed (version compatible with the project, e.g., JDK 8 or higher).

## Clone the repository:
   ```bash
   git clone https://github.com/KevithMannage/programming_language.git
   cd programming_language
   ```
   open in vscode using `code .`

## Usage
Run the RPAL interpreter using the following commands. Replace ./path/to/file/ with the path to your RPAL source file (e.g., program.rpal).

1. Evaluate Code and Print Result
```bash
java rpal input.txt
```
2. Print the Abstract Syntax Tree (AST)
```bash
java rpal -ast input.txt
```
3. Print the Standardized Tree (ST)
```bash
java rpal -st input.txt
```
4. Print AST, ST, and Evaluation Result
```bash
java rpal -ast -st input.txt
```
5. Compile Modified Code
Recompile any changed Java files before re-running:
```bash
javac ./src/*.java
```
6. Running the Program with Input Files in a Different Directory

If the input file is located in a directory other than the root directory, replace `input.txt` with the relative path to the file, prefixed with `./`.

**Example:**

```bash
java rpal ./Test/test_1.txt
```

## Notes
  - Ensure all file paths are correct and point to valid RPAL source files or Java source files.
  - The commands assume the Java classpath includes the compiled RPAL interpreter classes.
  - If errors occur (e.g., file not found, classpath issues), verify the file paths and Java environment setup.

   
