<?php
	/*
	 * DESCRIPTION: 
	 * 	 provides utility xml functions that are used by multiple functions
	 *   or php files. designed to cut down on copy n pasting.
	 *   
	 */
	
	/* function loadQuiz($quizFile, $quizIndex)
	 * 
	 * FUNCTION DESCRIPTION:
	 *   loads up the specified quiz from the xml file $quizFile at quiz index $quizIndex
	 * 
	 * PARAMETERS:
	 * 	 $quizFile - string - path to quiz XML file
	 * 	 $quizIndex - int - index of quiz in XML file
	 * 
	 * RETURN VALUE:
	 *   simplexml xml file structure for THE QUIZ ONLY
	 *   
	 * USAGE:
	 * 	 include 'quizzyXML.php';
	 *   $quiz = loadQuiz($quizFile, $quizIndex);
	 *   
	 */
	function loadQuiz($quizFile, $quizIndex)
	{
		//load up the quiz
		$quizXML= simplexml_load_file($quizFile);
		$quiz = $quizXML->quiz[$quizIndex];
		return $quiz;
	}
?>