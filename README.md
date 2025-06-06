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
- **Make (GnuWin)**
   - ## Installing Make on Windows
      1. Download the [GnuWin Make setup](https://sourceforge.net/projects/gnuwin32/files/make/3.81/make-3.81.exe/download?use_mirror=webwerks&download) and install it.
      2. **Add Make to your system PATH environment variable:**
         - Open the Start Menu and search for "Environment Variables".
         - Click "Edit the system environment variables".
         - In the System Properties window, click "Environment Variables".
         - Under "System variables", find and select the `Path` variable, then click "Edit".
         - Click "New" and add the path to the GnuWin32 `bin` directory (e.g., `C:\Program Files (x86)\GnuWin32\bin`).
         - Click OK to close all dialogs.
      3. Open a new terminal and run:
         ```sh
         make --version
         ```
         to verify that `make` is installed.

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

## Makefile Usage

The provided [makefile](makefile) allows you to easily compile, run, clean, and test the project.

### Compile all Java files

```sh
make all
```

### Run the interpreter with the default input

```sh
make run
```

### Clean compiled `.class` files

```sh
make clean
```

### Run test cases

Each test target runs the interpreter with a specific test file in the `Test` directory. For example:

```sh
make test1   # Runs java myrpal ./Test/test_1
make test2   # Runs java myrpal ./Test/test_2
...
make test13  # Runs java myrpal ./Test/test_13
```

## Notes
  - Ensure all file paths are correct and point to valid RPAL source files or Java source files.
  - The commands assume the Java classpath includes the compiled RPAL interpreter classes.
  - If errors occur (e.g., file not found, classpath issues), verify the file paths and Java environment setup.

   
