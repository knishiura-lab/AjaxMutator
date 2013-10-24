<?php
  include 'quizzyConfig.php';
	/*
	 * DESCRIPTION: 
	 * 	 used in the AJAX of the core quizzy php. serves the div framework of
	 *   of the quiz (i.e. puts enough question divs in there, sets proper ids etc.
	 * 
	 * PARAMETERS passed in GET:
	 *  _GET['quizFile']       xml file to open
	 *  _GET['quizIndex']      index of requested quiz in xml file
	 *   
	 */
	$quizFile = $cwd.'/'.$quizFolder.'/'.$_GET['quizFile'];
	$quizIndex = intval($_GET['quizIndex']);
		
	//load up the quiz
	include 'quizzyXML.php';
	$quiz = loadQuiz($quizFile, $quizIndex);	
	
	//find the number of questions and dump it to a javascript variable
	$numQuestions = count($quiz->question);
?>
  
  <script language="JavaScript">
    numQuestions = <?php echo $numQuestions; ?>;
  </script>

<?php	
	//and the name of the quiz
	$quizTitle = $quiz->title;
	
	//return that (insane) div structure
	//make the containers
?>

	<div class="quizzy_title"> <?php echo $quizTitle; ?> </div>
	<div id="quizzy_q_c">

	<?php
		//make a div for all the questions
		for($qi=0; $qi < $numQuestions + 1; $qi++)
		{
	?>
	
		<div class="quizzy_q" id="quizzy_q<?php echo $qi; ?>" style="width: <?php echo $quizWidth; ?>">&nbsp;</div>
	
	<?php
			//end the loop
		}
		//close the containers
	?>
	
</div>