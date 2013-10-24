//NOTE: QuizzyConfig.js MUST be included BEFORE this file!

//current quiz state
var quizFile = "";
var quizIndex = -1;
var curQuestion = -1;
var score = 0;

// この変数はユーザがラジオボタンのどれかをクリックした時に設定される
//this is set by jquery when the user clicks on one of the radio buttons
var selOpt = 0;
// これらの変数は説明文がDivに設定される時に設定される
//these are set when the explanation data is dropped into its div
var correctOpt = -1;
var addScore = 0;
var bestScore = 0;
var optValues;
// これらは上記以外のタイミングで，Ajaxによる非同期通信で呼ばれるPHPによって設定される
//these are set at other times by dropped in php code from ajax calls
var numQuestions = -1;
var quizWidth = -1;
var quizHeight = -1;

// documentが読み込まれるタイミングでクイズの読み込みを開始する
//When the document is read, start loading up the quiz
$(document).ready(function() {
  // ローディングメッセージの見せ方周りの設定
	$.loading.pulse = loadingPulse;
	$.loading.align = loadingAlign;
	$.loading.onAjax = true;	//don't change this!
	//$.loading.delay = loadingDelay;
	// 変数を全てリセット
	//reset all the variables
	quizFile = "";
	quizIndex = -1;
	curQuestion = -1;
	score = 0;
	selOpt = 0;
	correctOpt = -1;
	addScore = 0;
	
	// 読み込みメッセージの表示
	//put up a loading message
	$('#quizzy').loading(true);
	
	// クイズ一覧を読み込む
	// 生成されるラジオボタンはonClickイベントを実装しており(サーバ側でイベントが追加されている)，そこで処理を行う
	//load the quiz list
	//the buttons have onClick events so they're handled up there
	$.get('quizzy/serveQuizzes.php', function(data){
		$('#quizzy_load').html(data);
		
		// 説明文を隠す
		//hide the descriptions
		$('.quizzy_quiz_desc').hide();
		
		// ラジオボタンのラベル部分にクリックイベントを追加
		//add a click event to the radio buttons' label
		$('.quizzy_quiz_lbl').click(function () {
		  // ユーザがある選択肢をクリックした場合
			//the user clicked on one of the options
		  // IDの取得
			//get the id
			var thisId = $(this).attr('id');
			
			// IDからそのインデックスを習得(計算)し，selOpt変数にインデックスを代入
			//hack out the index and set selOpt to it
			var selQuiz = thisId.substring(thisId.lastIndexOf("lbl") + 3) * 1;
			
			// ラジオボタンが選択されている状態にする
			//make sure that the radio button is selected
			$('#quizzy_quiz_opt'+selQuiz).click();
		});
		
		// ラジオボタンに上とは別のクリックイベントを追加する
		//add another click event handler to the radio buttons
		$('.quizzy_quiz_opt').click(function() {
      // ユーザがある選択肢をクリックした場合
      //the user clicked on one of the options
      // IDの取得
      //get the id
			var thisId = $(this).attr('id');

      // IDからそのインデックスを習得(計算)し，selOpt変数にインデックスを代入
			//hack out the index and set selOpt to it
			var selQuiz = thisId.substring(thisId.lastIndexOf("opt") + 3) * 1;
			
			// 他の説明文をスライドアップ(下から上へ消えていくアニメーション)し，正しい説明文のみスライドダウンする
			//slide up all other descriptions while sliding down the correct one
			$('.quizzy_quiz_desc[id!=quizzy_quiz_desc'+selQuiz+']').slideUp(slideSpeed, function() {
				$('#quizzy_quiz_desc' + selQuiz).slideDown(slideSpeed);
			});
		});
		
		// サブミットボタンにクリックイベントを登録する
		//set the click event on the submit button
		$('#quizzy_start_b').click(startQuiz);
	});
	
});

// サーバに対して，クイズのセットアップをリクエストする
//requests a quiz setup from the server
function startQuiz()
{
  // 何かのクイズが選択されていることを確認する
	//make sure that there's a quiz that is selected
	if(quizIndex < 0)
		return;
		
	// このボタンに現在登録されているクリックイベントを解除する
	//unbind the click events for this button
	$(this).unbind();
	
	// グローバル変数は，ユーザがラジオボタンをクリックした際に適切に設定されている
	//globals were already set when the user clicked on the radio buttons
	
	// クイズの選択肢たちをフェードアウトさせる
	//fade out quiz options
	$('.quizzy_quiz_b').fadeOut(fadeSpeed);
	
	// Throbber (Now loadingのくるくるまわるアレ）を表示
	//put up throbber
	$('#quizzy').loading(true);
	
	// GETパラメータは以下の通り:
  //  _GET['quizFile']       開きたいxmlファイルの名前
  //  _GET['quizIndex']      求めるクイズのxmlの中でのインデックス
	//parameters passed in GET:
	//  _GET['quizFile']       xml file to open
	//  _GET['quizIndex']      index of requested quiz in xml file
	$.get('quizzy/serveQuiz.php', {quizFile: quizFile, quizIndex: quizIndex}, function(data){
		// Throbberの表示
	  //put up throbber
		$('#quizzy').loading(true);
		
		// クイズデータを取得したので，そのまま適切なdivの中に読み込む
		//we got our quiz datas, just dump them into the correct div
		$('#quizzy_quiz').html(data);
		
		// クイズの問題数も取得したので，いくつかのdivをリサイズする
		//we also got a numQuestions set, need to resize a few divs.
		$('#quizzy_c').width((numQuestions + 3) * quizWidth);
		$('#quizzy_quiz').width((numQuestions + 2) * quizWidth);
		$('.quizzy_title').width(quizWidth);
		
		// 次の問題をリクエストする
		//now request the next question
		requestNextQuestion();
	});
}

// 問題データをサーバに対してリクエストする
//requests a question from the server
function requestNextQuestion()
{		
	$('#quizzy_q' + curQuestion + '_foot_nxt').fadeOut(fadeSpeed, function() {
		$(this).attr('disabled', true);
	});
	
  // GETパラメータは以下の通り:
  //  _GET["quizFile"]       クイズデータを格納するxmlファイル名
  //  _GET["quizIndex"]      求めるクイズのxmlの中でのインデックス
  //  _GET["questNo"]        何番目の問題データを取得するか(0から始まる)
  //  _GET['score']          現在のユーザのスコア (最後の画面で必要)
	//parameters passed in GET:
	//  _GET["quizFile"]       xml file to open
	//  _GET["quizIndex"]      index of requested quiz in xml file
	//  _GET["questNo"]        question to return [first question is number 0]
	//  _GET['score']          score the player currently has (needed for serving last page)
	$.get('quizzy/serveQuestion.php', {quizFile: quizFile, quizIndex: quizIndex, questNo: (curQuestion + 1), score: score}, function(data){
		// 次の問題を取得した（のでインデックスを+1)
	  //we are now on the next question
		curQuestion++;
		
		// 適切なスタイルシートを書き換える
		//set necessary styles
		$('.quizzy_q').width(quizWidth);
		
		// 取得したデータ（HTML)を適切なdivに反映
		//dump the received data into the correct question div
		$("#quizzy_q" + curQuestion).html(data);
	
		// チェックボタン，次へボタン，説明文，値を表示するSPANを非表示，ユーザ操作を無効化する
		//hide and disable the check and next buttons, the explanation div, and the value spans
		$('#quizzy_q' + curQuestion + '_foot_chk').attr('disabled', true).hide();
		$('#quizzy_q' + curQuestion + '_foot_nxt').attr('disabled', true).hide();
		$('#quizzy_q' + curQuestion + '_exp').hide();
		$('.quizzy_q_opt_val').hide();
		
		// ユーザが最初の選択肢をクリックしたらselOptを0, ２番めの選択肢をクリックしたらselOptを1, ...
		// という風に設定できるように，クリックイベントを登録する
		//add click handlers so that when a user clicks on any first option, it sets selOpt to 0
		//and if they click on any 2nd option, it sets selOpt to 1, etc.
		$('.quizzy_q_opt').click(function (){
      // ユーザがある選択肢をクリックした場合
      //the user clicked on one of the options
      // IDの取得
      //get the id
			var thisId = $(this).attr('id');

      // IDからそのインデックスを習得(計算)し，selOpt変数にインデックスを代入
			//hack out the index and set selOpt to it
			selOpt = thisId.substring(thisId.lastIndexOf("opt") + 3) * 1;
			
			// ラジオボタンが選択されているようにする
			//make sure that the radio button is selected
			$('#quizzy_q'+curQuestion+'_opt'+selOpt+'_b').attr("checked", "checked");
		});
		
		// チェックボタン，次へボタンにクリックイベントを登録する
		//add the click event to the check and next buttons
		$('#quizzy_q' + curQuestion + '_foot_chk').click(checkQuestion);
		$('#quizzy_q' + curQuestion + '_foot_nxt').click(function (){
			$('#quizzy').loading(true);   
			$(this).unbind();
			requestNextQuestion();
		});
		
		// 最初の問題を解いているときはquizzy_cを右へ，そうでないときはquizzy_q_cを右へスライド
		//slide quizzy_c to the right if we're on question 0, quizzy_q_c otherwise
		var scrollSel = (curQuestion == 0) ? '#quizzy_c' : '#quizzy_q_c';
		var scrollAmt = (curQuestion == 0) ? (-quizWidth * (curQuestion + 1)) : (-quizWidth * (curQuestion));
		$(scrollSel).animate({left: scrollAmt + "px"}, slideSpeed, animateStyle, function(){
			// 最後の問題ボタンを非選択状態にする
		  //uncheck the last question's buttons
			$('.quizzy_q_opt_b').attr('checked', false);
			
			// チェックボタンをフェードインする
			//fade in the check button
			$('#quizzy_q' + curQuestion + '_foot_chk').attr('disabled', false).fadeIn(fadeSpeed);
		});
	});
}

function checkQuestion()
{ 
  // ユーザはquizFile内のquizIndex番目のクイズのcurQuestion番目の問題においてselOptを選択
	//the user has selOpt selected on question curQuestion
	//on the quizIndex'th quiz in quizFile
	
  // ユーザがどれかひとつしか選択してないことを確認
	//make sure the user selected one
	if( $('.quizzy_q_opt_b:checked').length == 0 )
		return;
	
	// クリックイベントを解除
	//unbind the click event
	$(this).unbind();
	
	// ボタンを隠す
	//hide the button
	$('#quizzy_q' + curQuestion + '_foot_chk').fadeOut(fadeSpeed, function() {
		$(this).attr('disabled', true);
	});
	
	// Throbberを表示
	//put up throbber
	$('#quizzy').loading(true);
	
	// この選択肢に対する説明文を取得する．これによってcorrectOpt変数の値が設定されることがありうる．
	//get the explanation for this option, it will set the correctOpt variable
	// GETパラメータは以下の通り：
	// _GET['quizFile']        クイズを格納するデータファイル
	// _GET['quizIndex']       xmlファイル内で何番目のクイズか
	// _GET['questNo']         何番目の問題か
  // _GET['selOpt']          説明文を取得したい選択肢の番号
	//parameters passed in GET:
	//  _GET['quizFile']       xml file to open
	//  _GET['quizIndex']      index of requested quiz in xml file
	//  _GET['questNo']        question to return (first is 1)
	//	_GET['selOpt']				 the option for which to retrieve the explanation
	$.get('quizzy/serveExplanation.php',  {quizFile: quizFile, quizIndex: quizIndex, questNo: curQuestion, selOpt: selOpt}, function(data) {
		
	  // Ajaxによる非同期通信によってデータを取得しているので，適切なdivにセットする
		//have the data returned by that ajax query, set the proper div info
		$('#quizzy_q' + curQuestion + '_exp').html(data);
		// この操作によってcorrectOptおよびaddScore変数は値を代入されているだろう
		//that should have set the correctOpt and addScore variables
		
		// スコアを更新
		//add to score
		score += addScore;
		
		// 現在の問題が部分点方式かどうかのフラグをセット
		//determine if this question has partial credit
		var partialCredit = false;
		for(var i in optValues)
			if(optValues[i] != 0 && optValues[i] != bestScore)
				partialCredit = true;
			
		// 値を表示する
		//show the values
		for( i in optValues ) {
			
		  // 部分的方式でないなら，正誤を示すためにXと✔を利用
			//if the question no partial credit, use an X or a ✓ to indicate correctness
			var toWrite = optValues[i];
			if(!partialCredit)
				toWrite = (optValues[i] == bestScore) ? '✓' : 'X';
			
			// ベストスコアならquizzy_opt_bestクラスを，ベストとワーストの間ならquizzy_opt_midを，
			// ワーストならquizzy_opt_worstをそれぞれ利用
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
		
		// slideUpWaitミリ秒待つ
		//wait slideUpWait millisec
		setTimeout(function() {
		  // 選択された答えと，最善の答え以外をスクロールアップする
			//scroll up all but the selected answer and the best answer
			var correctSel = '[id!=quizzy_q' + curQuestion + '_opt' + correctOpt + ']';
			var pickedSel = '[id!=quizzy_q' + curQuestion + '_opt' + selOpt + ']';
			if(addScore == bestScore)
				correctSel = '';
			$('.quizzy_q_opt' + correctSel + pickedSel).slideUp(slideSpeed);
			
			// expFadeInWatミリ秒待つ
			//wait expFadeInWait millisec
			setTimeout(function() {
				
			  // 説明文をフェードイン
				//fade in explanation
				$('#quizzy_q' + curQuestion + '_exp').fadeIn(fadeSpeed);
				
				// nextFadeInWaitミリ秒待つ
				//wait nextFadeInWait millisec
				setTimeout(function() {
					
				  // 次へボタンをフェードイン
					//fade in next button
					$('#quizzy_q' + curQuestion + '_foot_nxt').attr('disabled', false).fadeIn(fadeSpeed);
					
				}, nextFadeInWait); //wait nextFadeInWait ms to fade in the next button
				                    // 次へボタンをフェードインするまでnextFadeInWaitミリ秒待つ
				
			}, expFadeInWait); 		//wait expFadeInWait ms to fade in explanation
			                      // 説明文をフェードインするまでexpFadeInWaitミリ秒待つ
			
		}, slideUpWait); 			//wait scrollupwait ms to scroll up all but best answer
		                      // 最善の選択肢以外をスクロールアップするまでscrollupwaitミリ秒待つ
		
	});
}

function restartQuizzy()
{
  // 問題をスクロールして戻るまで，どれだけのアニメーションが必要か計算
	//figure out how much of the animation is in scrolling the questions back
	var firstRatio = curQuestion / (curQuestion + 1);
	// コンテナの中をスクロールして戻るにはどれだけ動くべきか
	//and how much is in scrolling the big container over
	var secondRatio = 1.0 - firstRatio;
	
	// 状態変数をリセット
	//reset all the state variables
	quizFile = "";
	quizIndex = -1;
	curQuestion = -1;
	score = 0;
	selOpt = 0;
	correctOpt = -1;
	addScore = 0;
	
	// 現在選択されているクイズをすべて非選択
	//unselect any selected quiz
	$('.quizzy_quiz_opt').attr('checked', false);
	// すべての選択肢を非表示
	//hide all the descriptions
	$('.quizzy_quiz_desc').hide();
	
	// quizzy_q_cを最初までスクロールして戻る
	//scroll the quizzy_q_c back to the start
	$('#quizzy_q_c').animate({left: "0px"}, firstRatio * restartSpeed, animateStyle, function(){
		
	  // quizzy_cを最初まで戻る
		//scroll the quizzy_c back to the start
		$('#quizzy_c').animate({left: "0px"}, secondRatio * restartSpeed, animateStyle, function(){

		  // サブミットボタンのクリックイベントを元に戻す
			//reset the click event on the submit button
			$('#quizzy_start_b').click(startQuiz);
			
			// クイズ選択ボタンをフェードインする
			//fade the quiz select buttons back in
			$('.quizzy_quiz_b').fadeIn(fadeSpeed);
			
		}); //quizzy_c
	}); //quizzy_q_c
}
