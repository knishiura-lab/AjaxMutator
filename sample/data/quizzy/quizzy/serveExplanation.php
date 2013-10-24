<?php
  include 'quizzyConfig.php';
  /*
   * DESCRIPTION: 
   * 	 Used as an AJAX call in the core quizzy javascript file. Returns the passed 
   *   quiz file and index's option's explanation. Also sets two javascript variables
   *   usinga  dropped in <script> tag. 
   * 
   * PARAMETERS passed in GET:
   *  _GET['quizFile']       xml file to open
   *  _GET['quizIndex']      index of requested quiz in xml file
	 *  _GET['questNo']        question to return (first is 0)
   *	_GET['selOpt']				 the option for which to retrieve the explanation
   */

	$quizFile = $cwd.'/'.$quizFolder.'/'.$_GET['quizFile'];
	$quizIndex = intval($_GET['quizIndex']);
  $questNo = intval($_GET['questNo']);
  $selOpt = intval($_GET['selOpt']);
	
	//load up the quiz
	include 'quizzyXML.php';
	$quiz = loadQuiz($quizFile, $quizIndex);	
  
  //represents where this quiz's pictures should be found
  $picDir = 'quizzy/'.$quizFolder.'/'.$picFolder.'/';
	
	//get the requested question
  $quest=$quiz->question[$questNo];
  
  //then the requested option and explanation
  $opt = $quest->option[$selOpt];
  $exp = $opt->explanation;
  
	//get the max possible score
	$maxScore = intval($quest->maxscore);
  
  //set the javascript variables
  //get the score to add for this option
  $addScore = intval($opt->score);
?>
  <script language='JavaScript'>
    optValues = new Array();
    addScore = <?php echo $addScore ?>;
    
<?php
  //figure out what the highest possible score
  $i = 0;
  $bestScore = 0;
  $correctOpt = -1;
	foreach($quest->option as $opt)
	{
	  $curScore = intval($opt->score);
?>
    //set optValue[]
    optValues[<?php echo $i ?>] = <?php echo $curScore ?>;
<?php
    if($curScore > $bestScore)
    {
      $bestScore = $curScore;
      $correctOpt = $i;
    }
    ++$i;
  }
  //echo a script tag that sets these variables
?>
    correctOpt = <?php echo $correctOpt ?>;
    bestScore = <?php echo $bestScore ?>;
  </script>
  
  <!-- thetwo divs with correct info -->
	  <?php if(isset($exp->img)) { ?>
		  <img src="<?php echo $picDir.$exp->img['src'];?>" alt="<?php echo $exp->img['alt'];?>" >
    <?php } ?>
		<p><?php echo $exp->text ?></p>