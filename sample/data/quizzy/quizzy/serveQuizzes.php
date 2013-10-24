<?php 
  include 'quizzyConfig.php';
  //open the quizzes dir
  $quizDir = dir($quizFolder);
  
  //represents where this quiz's pictures should be found
  $picDir = 'quizzy/'.$quizFolder.'/'.$picFolder.'/';
  
  echo '<div class="quizzy_load_body">';
	echo '<h1>'.$pickQuizMessage.'</h1>';
  
  //List files in quizzes directory
  $j = 0;
  while (($file = $quizDir->read()) !== false)
  {
  	//don't try to open . or ..
  	if(( $file == '.')||( $file == '..') ) continue;
  	
    //make sure we have proper extension
    if(!strpos(strtolower($file), 'xml'))
      continue;
    
  	//open the current file
  	$filename = $cwd.'/'.$quizFolder.'/'.$file;
  	$quizXML= simplexml_load_file($filename);
  	
  	//make a list of all the quizzes in this xml file
  	$i=0;
  	foreach ($quizXML->quiz as $curQuiz){
?>
  		<p>
  		  <input type="radio" class="quizzy_quiz_opt" id="quizzy_quiz_opt<?php echo $j; ?>" onClick="quizFile = '<?php echo basename($filename); ?>'; quizIndex = <?php echo $i; ?>;" name="quizzy_quiz_sel">
        <label class="quizzy_quiz_lbl" id="quizzy_quiz_lbl<?php echo $j; ?>"><?php echo $curQuiz->title; ?></label>
        <?php if(isset($curQuiz->img)) { ?>
          <img src="<?php echo $picDir . $curQuiz->img['src']; ?>" alt="<?php echo $curQuiz->img['alt']; ?>" > 
        <?php 
        }
        if(isset($curQuiz->description)) { 
        ?>
        <br >
          <div id="quizzy_quiz_desc<?php echo $j; ?>" class="quizzy_quiz_desc">
            <?php 
              if(isset($curQuiz->description->img)) { 
                echo '<img src="'.$picDir.$curQuiz->description->img['src'].'" alt="'.$curQuiz->description->img['alt'].'" >';
              } 
            ?>
            <?php echo $curQuiz->description->text; ?>
          </div>
          <?php } ?>
       </p>
<?php
  		++$i; ++$j;
  	}
  
  }
?></div>
    <div class="quizzy_load_foot"><input type="submit" class="quizzy_b" id="quizzy_start_b" value="Start Quiz"></div>
