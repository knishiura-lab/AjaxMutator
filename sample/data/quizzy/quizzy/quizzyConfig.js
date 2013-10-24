//JavaScript-Only Global Variables

//how fast fading animations should be completed (in ms)
var fadeSpeed = "def";

//how fast sliding animations should be completed (in ms)
var slideSpeed = "def";

//how to animate movement. can be linear or swing
var animateStyle = "swing";

//how long to wait in ms before scrolling up non-100% options
var slideUpWait = 500;

//how long to wait in ms before fading in the explanation
var expFadeInWait = 200;

//how long to wait in ms before fading in the next button
var nextFadeInWait = 500;

//time in ms it takes for the quiz to restart
var restartSpeed = 500;


//SETTINGS for jQuery Loading

//The pulse animation to use. Can be one of the following:
//'working error'		-- displays 'Loading...' for 10 seconds, then changes to 'Still Working...' for 100 seconds,
//										 and changes to 'Task may have failed'. All messages are static
//'error' 				 	-- displays 'Loading...' for 100 seconds, then changes to 'Task may have failed'. All messages are static 
//'type' 						-- "types" the text 'Loading...', so it displays 'L', then 'Lo', then 'Loa', then 'Load', etc.
//'ellipsis'				-- "types" the epllipsis after 'Loading', so it displays 'Loading', then 'Loading.', then 'Loading..', etc
//'fade'						-- displays 'Loading...' and fades the div in and out
//'fade error'			-- displays 'Loading...' and fades the div in and out for 100 seconds and changes the message to a static 'Task may have failed.'
//'working type' 		-- "types" 'Loading...' for 10 seconds then changes to "type" 'Still Working...'
// note that these can generally be combined to produce the desired effect like how 'working type' is a combination of 'working' and 'type'
var loadingPulse = 'ellipsis';

//where to put the loading message. in format of '[vertical align]-[horizontal align]' unless center center then it's just 'center'
//vertical line can be 'top', 'center', or 'bottom', horizontal align can be 'left', 'center', or 'right'
//so if you want it in the top left, you set loadingAlign to 'top-left'.
var loadingAlign = 'bottom-left';

//how long to wait before putting the loading message up in milliseconds.
//This setting will not do anything as of this version because the delay feature of loading is broken.
var loadingDelay = 300;