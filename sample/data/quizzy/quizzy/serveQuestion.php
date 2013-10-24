<?php
  include 'quizzyConfig.php';
	/*
	 * DESCRIPTION: 
	 * 	 used in the AJAX of the core quizzy php. serves up ONE question of the
	 *   requested quiz. 
	 * 
	 * PARAMETERS passed in GET:
	 *  _GET['quizFile']       xml file to open
	 *  _GET['quizIndex']      index of requested quiz in xml file
	 *  _GET['questNo']        question to return (first is 0)
	 *  _GET['score']          score the player currently has (needed for serving last page)
	 *   
	 */
	$quizFile = $cwd.'/'.$quizFolder.'/'.$_GET['quizFile'];
	$quizIndex = intval($_GET['quizIndex']);
  $questNo = intval($_GET['questNo']);
  $score = intval($_GET['score']);
	
	//load up the quiz
	include 'quizzyXML.php';
	$quiz = loadQuiz($quizFile, $quizIndex);	
  
  //represents where this quiz's pictures should be found
  $picDir = 'quizzy/'.$quizFolder.'/'.$picFolder.'/';
	
  //see if the requested question doesn't exist
  if($questNo >= count($quiz->question))
  {
    //see what the max possible score for the quiz was
    $maxPossible = 0;
    foreach($quiz->question as $quest) {
      $thisBest = 0;
      foreach($quest->option as $opt) {
        if(intval($opt->score) > $thisBest)
          $thisBest = $opt->score;
      }
      $maxPossible += intval($thisBest);
    }
?>
    <div class="quizzy_result">
      <h1><?php echo $endQuizMessage; ?></h1>
      <p>You scored <span class="quizzy_result_score"><?php echo $score;?></span> out of 
      <span class="quizzy_result_max"><?php echo $maxPossible;?></span> possible points!</p>
    
<?php
  
  //figure out a percentage score, then use the grading information in the xml data to put some more stuff up
  $percentage = ($score / $maxPossible) * 100;
  
  //find the correct scoreRange
  $scoreRange = NULL;
  foreach($quiz->grading->range as $range) {
    //take care of 0 boundary case in easiest way possible.
    if(intval($range['start']) == 0) $range['start'] = -1;
    if(intval($range['start']) < $percentage && intval($range['end']) >= $percentage) {
      $scoreRange = $range;
      break;
    }
  }
  
?>
    
      <p>Grade: <span class="quizzy_result_grade quizzy_result_grade_<?php echo $scoreRange->grade; ?>"><?php echo $scoreRange->grade; ?></span> (<?php printf('%.1f%%', $percentage); ?>)</p>
      <?php if(isset($scoreRange->img)) { ?>
        <div class="quizzy_result_img"><img src="<?php echo $picDir . $scoreRange->img['src'];?>" alt="<?php echo $scoreRange->img['alt'];?>" ></div>
      <?php } ?>
      <p class="quizzy_result_rank quizzy_result_rank_<?php echo $scoreRange->rank; ?>"><?php echo $scoreRange->rank; ?></p>
      <div class="quizzy_result_foot"><input type="submit" onclick="restartQuizzy();" value="Do a different Quiz"></div>
    </div>
<?php
    return;
  }
  
	//get the requested question
  $quest=$quiz->question[$questNo];
?>

<div class="quizzy_q_body">
  <?php if(isset( $quest->img )) { ?>
	  <img src="<?php echo $picDir.$quest->img['src'];?>" alt="<?php echo $quest->img['alt'];?>" >
  <?php } ?>
	<p><?php echo $quest->text; ?></p>
</div>
<div class="quizzy_q_opts">
		
<?php
	//loop through all of the options for this question
  $oi = 0;
	foreach($quest->option as $opt)
	{
?>
		<p class="quizzy_q_opt" id="quizzy_q<?php echo $questNo; ?>_opt<?php echo $oi; ?>">
			<!-- input type=radio class=quizzy_q_opt id=quizzy_q[qi]_opt[oi] name=quizzy_q[qi] -->
			<input type="radio" name="quizzy_q<?php echo $questNo; ?>" class="quizzy_q_opt_b" id="quizzy_q<?php echo $questNo; ?>_opt<?php echo $oi; ?>_b">
			<label>
				<?php echo $opt->text; ?>
        <?php if(isset($opt->img)) { ?>
          <img src="<?php echo $picDir.$opt->img['src'];?>" alt="<?php echo $opt->img['alt'];?>" >
        <?php } ?>
        <span class="quizzy_q_opt_val" id="quizzy_q<?php echo $questNo ?>_opt<?php echo $oi; ?>_val"></span>
			</label>
		</p>
		
<?php
		//finish loop
		$oi++;
	}
	//end list
?>
<div class="quizzy_q_exp" id="quizzy_q<?php echo $questNo ?>_exp"></div>

</div><!--options-->


<div class="quizzy_q_foot">
	<input type="submit" class="quizzy_q_foot_b" id="quizzy_q<?php echo $questNo ?>_foot_chk" value="Check Answer">
	<input type="submit" class="quizzy_q_foot_b" id="quizzy_q<?php echo $questNo ?>_foot_nxt" value="Next">				
</div>