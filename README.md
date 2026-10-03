# MicroJava Compiler

Compiler for the MicroJava language, built for the Compilers course (Programski prevodioci 1) at ETF Belgrade.

## Features
- Lexical analysis (JFlex)
- LALR(1) parsing with error recovery (CUP)
- Semantic analysis with symbol table
- (Code generation for MicroJava VM)   ← obriši ako nisi radila

## Tech
Java, JFlex, CUP, Ant

## Build & run
ant compile
java -cp bin:lib/* rs.ac.bg.etf.pp1.Compiler test/program.mj

## Example
(kratak .mj program i šta kompajler ispiše)
