IE 6 quirks:
 - loading message does not work (users will not be told when AJAX calls are being loaded)
 - images are constrained by having a width or height set, since max-width & max-height do not work

Opera quirks:
 - when scrolling to the second question (and only that one) the first question scrolls from off the 
 		right side of the screen (instead of its current location). I have no idea why.

Overall quirks:
 - quizzes with descriptions that have pictures will slide down in an.... odd... fashion. It works, but could
 		look much better. It's being worked on... top bug really. 
 - if you skin the quiz to be position: absolute, loading stops working.

Browser compatibility -- Tested and confirmed working (with above mentioned quirks) in the following browsers:
	IE 6, 7, and 8 (quirks and standards compliant mode)
	Firefox 1.5, 2, 3, and 3.5
	Opera 8, 9, and 10
	Safari 4 and 5
	Chrome
	Konqueror 4.3


To use: 
	1. extract quizzy and put the 'quizzy' directory into the same folder as your webpage.
	2. drop <?php include 'quizzy/quizzyHeader.php';?> in your <head> section
	3. put <?php include 'quizzy/quizzy.php'; ?> in your <body> section where you want the quiz
	4. edit quizzySettings.php and quizzySettings.js to your liking
	5. Create some quizzes and drop them into $quizFolder (probably 'quizzy/quizzes/')
	

Quiz XML formatting:
Any line in [square brackets] is optional, lines that have ... are places you can add more of that type
of element. Any picture you add has to be under the quizzes/[pictures directory] folder.
See quizzyConfig.php.

<?xml version="1.0" encoding="utf-8"?>
<quizzes>
  <quiz>
    <title>Quiz title</title>
    [<img src="foo.jpg" alt="bar" />]
    <description>
      <text>Quiz description</text>
      [<img src="foo.jpg" alt="bar" />]
    </description>
    <grading>
      <range start="0" end="60">
        <grade>F</grade>
        <rank>Rank</rank>
        [<img src="foo.jpg" alt="bar" />]
      </range>
      ...
    </grading>
    <question>
      <text>Question Text</text>
      [<img src="foo.jpg" alt="bar" />]
      <option>
        <text>Option Text</text>
      	[<img src="foo.jpg" alt="bar" />]
        <score>3</score>
        <explanation>
          <text>Explanation text</text>
      	  [<img src="foo.jpg" alt="bar" />]
        </explanation>
      </option>
      ...
    </question>
    ...
  </quiz>
  ...
</quizzes>



Future features:
 - Non-multiple choice options (text input and checkboxes ftw)					medium
 - Database integration.. to make quiz perfectly uncheatable by storing score on server		hard
 - Printing stylesheet										easy
