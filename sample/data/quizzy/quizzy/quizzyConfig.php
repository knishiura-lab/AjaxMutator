<?php
  //global variables
   
  //PLEASE NOTE:
  //There are several other JavaScript-only options configurable in the quizzyConfig.js file
  
  //where the quiz xml files are stored under the /quizzy directory
  //default is /quizzy/quizzes so by default the variable is set to 'quizzes'
  $quizFolder = 'quizzes';
  
  //where the quiz picture files are stored under /quizzy/$quizFolder
  //by default, it looks in the same folder as the quizzes. this might get
  //messy with a large number of quizzes so the option to move it out is given here
  $picFolder = 'img';
  
  //the dimensions of the quiz in pixels
  $quizWidth = 200;
  $quizHeight = 300;
  
  //The message to display above the list of quiz names that the user would select
  //this is put in an h1 tag
  $pickQuizMessage = 'Please Select a Quiz';
  
  //The message displayed at the end of the quiz before the user's score, grade, and rank
  //this is put in an h1 tag
  $endQuizMessage = 'Done!';

  $cwd = str_replace('\\', '/', getcwd());
?>