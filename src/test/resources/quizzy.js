// Note: This file is from quizzy project (http://quizzy.sourceforge.net/).

//NOTE: QuizzyConfig.js MUST be included BEFORE this file!

//current quiz state
var quizFile = "";
var quizIndex = -1;
var curQuestion = -1;
var score = 0;

//this is set by jquery when the user clicks on one of the radio buttons
var selOpt = 0;
//these are set when the explantion data is dropped into its div
var correctOpt = -1;
var addScore = 0;
var bestScore = 0;
var optValues;
//these are set at other times by dropped in php code from ajax calls
var numQuestions = -1;
var quizWidth = -1;
var quizHeight = -1;

//When the document is read, start loading up the quiz
$(document).ready(function() {
	$.loading.pulse = loadingPulse;
	$.loading.align = loadingAlign;
	$.loading.onAjax = true;	//don't change this!
	//$.loading.delay = loadingDelay;
	//reset all the variables
	quizFile = "";
	quizIndex = -1;
	curQuestion = -1;
	score = 0;
	selOpt = 0;
	correctOpt = -1;
	addScore = 0;
	
	//put up a loading message
	$('#quizzy').loading(true);
	
	//load the quiz list
	//the buttons have onClick events so they're handled up there
	$.get('quizzy/serveQuizzes.php', function(data){
		$('#quizzy_load').html(data);
		
		//hide the descriptions
		$('.quizzy_quiz_desc').hide();
		
		//add a click event to the radio buttons' label
		$('.quizzy_quiz_lbl').click(function () {
			//the user clicked on one of the options
			//get the id
			var thisId = $(this).attr('id');
			
			//hack out the index and set selOpt to it
			var selQuiz = thisId.substring(thisId.lastIndexOf("lbl") + 3) * 1;
			
			//make sure that the radio button is selected
			$('#quizzy_quiz_opt'+selQuiz).click();
		});
		
		//add another click event handler to the radio buttons
		$('.quizzy_quiz_opt').click(function() {
			//the user clicked on one of the options
			//get the id
			var thisId = $(this).attr('id');
			
			//hack out the index and set selOpt to it
			var selQuiz = thisId.substring(thisId.lastIndexOf("opt") + 3) * 1;
			
			//slide up all other descriptions while sliding down the correct one
			$('.quizzy_quiz_desc[id!=quizzy_quiz_desc'+selQuiz+']').slideUp(slideSpeed, function() {
				$('#quizzy_quiz_desc' + selQuiz).slideDown(slideSpeed);
			});
		});
		
		//set the click event on the submit button
		$('#quizzy_start_b').click(startQuiz);
	});
	
});

//requests a quiz setup from the server
function startQuiz()
{
	//make sure that there's a quiz that is selected
	if(quizIndex < 0)
		return;
		
	//unbind the click events for this button
	$(this).unbind();
	
	//globals were already set when the user clicked on the radio buttons
	
	//fade out quiz options
	$('.quizzy_quiz_b').fadeOut(fadeSpeed);
	
	//put up throbber
	$('#quizzy').loading(true);
	
	//parameters passed in GET:
	//  _GET['quizFile']       xml file to open
	//  _GET['quizIndex']      index of requested quiz in xml file
	$.get('quizzy/serveQuiz.php', {quizFile: quizFile, quizIndex: quizIndex}, function(data){
		//put up throbber
		$('#quizzy').loading(true);
		
		//we got our quiz datas, just dump them into the correct div
		$('#quizzy_quiz').html(data);
		
		//we also got a numQuestions set, need to resize a few divs.
		$('#quizzy_c').width((numQuestions + 3) * quizWidth);
		$('#quizzy_quiz').width((numQuestions + 2) * quizWidth);
		$('.quizzy_title').width(quizWidth);
		
		//now request the next question
		requestNextQuestion();
	});
}

//requests a question from the server
function requestNextQuestion()
{		
	$('#quizzy_q' + curQuestion + '_foot_nxt').fadeOut(fadeSpeed, function() {
		$(this).attr('disabled', true);
	});
	
	//parameters passed in GET:
	//  _GET["quizFile"]       xml file to open
	//  _GET["quizIndex"]      index of requested quiz in xml file
	//  _GET["questNo"]        question to return [first question is number 0]
	//  _GET['score']          score the player currently has (needed for serving last page)
	$.get('quizzy/serveQuestion.php', {quizFile: quizFile, quizIndex: quizIndex, questNo: (curQuestion + 1), score: score}, function(data){
		//we are now on the next question
		curQuestion++;
		
		//set necessary styles
		$('.quizzy_q').width(quizWidth);
		
		//dump the recieved data into the correct question div
		$("#quizzy_q" + curQuestion).html(data);
	
		//hide and disable the check and next buttons, the explanation div, and the value spans
		$('#quizzy_q' + curQuestion + '_foot_chk').attr('disabled', true).hide();
		$('#quizzy_q' + curQuestion + '_foot_nxt').attr('disabled', true).hide();
		$('#quizzy_q' + curQuestion + '_exp').hide();
		$('.quizzy_q_opt_val').hide();
		
		//add click handlers so that when a user clicks on any first option, it sets selOpt to 0
		//and if they click on any 2nd option, it sets selOpt to 1, etc.
		$('.quizzy_q_opt').click(function (){
			//the user clicked on one of the options
			//get the id
			var thisId = $(this).attr('id');
			
			//hack out the index and set selOpt to it
			selOpt = thisId.substring(thisId.lastIndexOf("opt") + 3) * 1;
			
			//make sure that the radio button is selected
			$('#quizzy_q'+curQuestion+'_opt'+selOpt+'_b').attr("checked", "checked");
		});
		
		//add the click event to the check and next buttons
		$('#quizzy_q' + curQuestion + '_foot_chk').click(checkQuestion);
		$('#quizzy_q' + curQuestion + '_foot_nxt').click(function (){
			$('#quizzy').loading(true);   
			$(this).unbind();
			requestNextQuestion();
		});
		
		//slide quizzy_c to the right if we're on question 0, quizzy_q_c otherwise
		var scrollSel = (curQuestion == 0) ? '#quizzy_c' : '#quizzy_q_c';
		var scrollAmt = (curQuestion == 0) ? (-quizWidth * (curQuestion + 1)) : (-quizWidth * (curQuestion));
		$(scrollSel).animate({left: scrollAmt + "px"}, slideSpeed, animateStyle, function(){
			//uncheck the last question's buttons
			$('.quizzy_q_opt_b').attr('checked', false);
			
			//fade in the check button
			$('#quizzy_q' + curQuestion + '_foot_chk').attr('disabled', false).fadeIn(fadeSpeed);
		});
	});
}

function checkQuestion()
{ 
	//the user has selOpt selected on question curQuestion
	//on the quizIndex'th quiz in quizFile
	
	//make sure the user selected one
	if( $('.quizzy_q_opt_b:checked').length == 0 )
		return;
	
	//unbind the click event
	$(this).unbind();
	
	//hide the button
	$('#quizzy_q' + curQuestion + '_foot_chk').fadeOut(fadeSpeed, function() {
		$(this).attr('disabled', true);
	});
	
	//put up throbber
	$('#quizzy').loading(true);
	
	//get the explanation for this option, it will set the correctOpt variable
	//parameters passed in GET:
	//  _GET['quizFile']       xml file to open
	//  _GET['quizIndex']      index of requested quiz in xml file
	//  _GET['questNo']        question to return (first is 1)
	//	_GET['selOpt']				 the option for which to retrieve the explanation
	$.get('quizzy/serveExplanation.php',  {quizFile: quizFile, quizIndex: quizIndex, questNo: curQuestion, selOpt: selOpt}, function(data) {
		
		//have the data returned by that ajax query, set the proper div info
		$('#quizzy_q' + curQuestion + '_exp').html(data);
		//that should have set the correctOpt and addScore variables
		
		//add to score
		score += addScore;
		
		//determine if this question has partial credit
		var partialCredit = false;
		for(var i in optValues)
			if(optValues[i] != 0 && optValues[i] != bestScore)
				partialCredit = true;
			
		//show the values
		for( i in optValues ) {
			
			//if the question no partial credit, use an X or a ✓ to indicate correctness
			var toWrite = optValues[i];
			if(!partialCredit)
				toWrite = (optValues[i] == bestScore) ? '✓' : 'X';
			
			//if it was best score, use quizzy_opt_best
			//in between best and worst, use quizzy_opt_mid
			//or the worst, use quizzy_opt_worst
			var useClass = 'quizzy_opt_worst';
			if(optValues[i] == bestScore)
				useClass = 'quizzy_opt_best';
			if(optValues[i] > 0 && optValues[i] < bestScore)
				useClass = 'quizzy_opt_mid';
			
			$('#quizzy_q' + curQuestion + '_opt' + i + '_val').html('<span class="' + useClass + '">' + toWrite + '</span>');
		}
		$('.quizzy_q_opt_val').fadeIn(fadeSpeed);
		
		
		//wait slideUpWait millisec
		setTimeout(function() {
			//scroll up all but the selected answer and the best answer
			var correctSel = '[id!=quizzy_q' + curQuestion + '_opt' + correctOpt + ']';
			var pickedSel = '[id!=quizzy_q' + curQuestion + '_opt' + selOpt + ']';
			if(addScore == bestScore)
				correctSel = '';
			$('.quizzy_q_opt' + correctSel + pickedSel).slideUp(slideSpeed);
			
			//wait expFadeInWait millisec
			setTimeout(function() {
				
				//fade in explanation
				$('#quizzy_q' + curQuestion + '_exp').fadeIn(fadeSpeed);
				
				//wait nextFadeInWait millisec
				setTimeout(function() {
					
					//fade in next button
					$('#quizzy_q' + curQuestion + '_foot_nxt').attr('disabled', false).fadeIn(fadeSpeed);
					
				}, nextFadeInWait); //wait nextFadeInWait ms to fade in the next button
				
			}, expFadeInWait); 		//wait expFadeInWait ms to fade in explanation
			
		}, slideUpWait); 			//wait scrollupwait ms to scroll up all but best answer
		
	});
}

function restartQuizzy()
{
	//figure out how much of the animation is in scrolling the questions back
	var firstRatio = curQuestion / (curQuestion + 1);
	//and how much is in scrolling the big container over
	var secondRatio = 1.0 - firstRatio;
	
	//reset all the state variables
	quizFile = "";
	quizIndex = -1;
	curQuestion = -1;
	score = 0;
	selOpt = 0;
	correctOpt = -1;
	addScore = 0;
	
	//unselect any selected quiz
	$('.quizzy_quiz_opt').attr('checked', false);
	//hide all the descriptions
	$('.quizzy_quiz_desc').hide();
	
	//scroll the quizzy_q_c back to the start
	$('#quizzy_q_c').animate({left: "0px"}, firstRatio * restartSpeed, animateStyle, function(){
		
		//scroll the quizzy_c back to the start
		$('#quizzy_c').animate({left: "0px"}, secondRatio * restartSpeed, animateStyle, function(){

			//reset the click event on the submit button
			$('#quizzy_start_b').click(startQuiz);
			
			//fade the quiz select buttons back in
			$('.quizzy_quiz_b').fadeIn(fadeSpeed);
			
		}); //quizzy_c
	}); //quizzy_q_c
}
