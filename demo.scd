Server.local.boot;

// initialize class object by passing ip + port of iOS / Android device
CNTRL.init("127.0.0.1", 8080); 

a = CNTRL.slider;		// create a slider filling the screen
a.value.postln;			// move slider on device and then execute this line to print its value

b = CNTRL.button;		// create new button and split device screen between it and the previous slider
b.value = 1;			// button on device screen toggled on
b.value = 0;			// button on device screen toggled off

// define a callback for the next button. The callback will be passed the widget when called 
// so that you can query and use its new value.
c = CNTRL.button( (\callback:{ arg widget; widget.value.postln; } ) );

// pass optional parameters for widget as dictionary. See Control website for details on
// parameters that can be used.
d = CNTRL.button( (label:"TEST", page:1, color:"#f00") );