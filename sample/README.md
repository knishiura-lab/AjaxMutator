Files in this folder are samples to demonstrate the usage of AjaxMutator.

### Background
To apply mutation analysis, you should already have two things; Test target application, and test
code. For this sample, test target application is a simple quiz application based on
[quizzy](http://quizzy.sourceforge.net/) library and is located in /data/quizzy folder.
Test code is in another module, [sample_test_case](https://github.com/knishiura-lab/AjaxMutator/tree/master/sample_test_case).

### setup instructions if you want to run this sample on your environment

1. Copy /data/quizzy files to your apache root
```
> sudo cp -r data/quizzy /var/www/.
```
You will be able to access app via http://localhost/quizzy/main.php

2. Make copied file modifiable
```
> sudo chmod -R a+rwX /var/www/quizzy
```

3. Setup `sample_test_case/src/main/resources/test.properties` file to specify Test target URL and
location of certain php file (Test case want to modify this file).
```
target_url=http://localhost/quizzy/main.php
config_php_file_path=/var/www/quizzy/quizzy/quizzyConfig.php
```
(At this point, you'll be able to run [sample test case](https://github.com/knishiura-lab/AjaxMutator/blob/master/sample_test_case/src/main/java/jp/gr/java_conf/daisy/ajax_mutator/sample/quizzy/QuizzyTest.java) )

4. Setup `sample/src/main/resources/project.properties` file to specify Test target URL and file
name of mutation target.
```
quizzy_url=http://localhost/quizzy/main.php
quizzy_js_path=/var/www/quizzy/quizzy/quizzy.js
```

5. Running [MutationAnalysisManager](https://github.com/knishiura-lab/AjaxMutator/blob/master/sample/src/main/java/jp/gr/java_conf/daisy/ajax_mutator/sample/quizzy/MutationAnalysisManager.java)
class to start mutation analysis.

### Implement your own MutationAnalysisManager
When using AjaxMutator, mutation analysis consists of three parts:

1. Parsing JavaScript program to collect part of program where AjaxMutator should focus
_MutateVisitor_ class is used to traverse JavaScript program. _Detector_ class can be used to tell which part of program you focus.
```
        MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
        builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
        MutateVisitor visitor = builder.build();
        MutationTestConductor conductor = new MutationTestConductor();
        conductor.setup(pathToJsFile, targetURL, visitor);
```

2. Define how will AjaxMutator *mutate* the detected program
_Mutator_ class can be used to specify how AjaxMutator mutate program.
```
        Set<Mutator> mutators = ImmutableSet.<Mutator>of(
                new EventTargetRAMutator(visitor.getEventAttachments()),
                new TimerEventDurationRAMutator(visitor.getTimerEventAttachmentExpressions()),
                new AttributeModificationValueRAMutator(visitor.getAttributeModifications()),
                new DOMSelectionSelectNearbyMutator(),
                new RequestOnSuccessHandlerRAMutator(visitor.getRequests()),
                new RequestUrlRAMutator(visitor.getRequests()));
```

3. 
Running test case for every mutants. MutationConductor#generateMutationsAndApplyTest take _TestExectuor_ class, for JUnit test case, you can just use _JUnitExecutor_.
```
        conductor.generateMutationsAndApplyTest(new JUnitExecutor(QuizzyTest.class), mutators);
```

4. If you modify test case referring result of step 3, you may want to run test cases again over mutants. In this case, you can just run
```
        conductor.mutationAnalysisUsingExistingMutations(new JUnitExecutor(QuizzyTest.class));
```
In this case, exisitng mutation file is reused.
