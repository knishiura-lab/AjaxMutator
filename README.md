### Overview
AjaxMutator is software to conduct [Mutation Testing (or Analysis)](http://en.wikipedia.org/wiki/Mutation_testing)
on JavaScript Web Applications.

Mutation analysis consists of two parts, namely,  
1) Creating mutants (faulty program) and 2) Running test on mutants

Using AjaxMutator, you can  
1) Specify what kind of mutation to apply and 2) Running JUnit test cases over mutants

You are also able to  
1) Define your own mutation operator and 2) Combining other test framework  
by overriding build-in classes

### Build
We use [Apache Maven](http://maven.apache.org/) for build management. If you have maven installed,
you can install AjaxMutator by
> mvn install

### Usage
See [/sample](https://github.com/knishiura-lab/AjaxMutator/tree/master/sample) to check how to use
AjaxMutator by running example.

### Publication
**Mutation Analysis for JavaScript Web Applications Testing**
Kazuki Nishiura, Yuta Maezawa, Hironori Washizaki and Shinichi Honiden  
The 25th International Conference on Software Engineering and Knowledge Engineering (SEKE'13), 159-165, June 2013

[Our GitHub page](http://knishiura-lab.github.io/AjaxMutator/) and
[Wiki](https://github.com/knishiura-lab/AjaxMutator/wiki) may contain more details.