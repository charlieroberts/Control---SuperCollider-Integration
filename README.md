Control - SuperCollider Integration
========

Use this class to automatically generate interface layouts on iOS / Android devices in SuperCollider.
You must be using the most recent development build of Control ( https://github.com/charlieroberts/Control )

Example code:

	Server.local.boot;

	// initialize class object by passing ip + port of iOS / Android device
	// this will automatically create a blank interface on the device
	CNTRL.init("127.0.0.1", 8080); 

	a = CNTRL.slider;		// create a slider filling the device screen
	a.value.postln;			// move slider on device and then execute this line to print its new value

	b = CNTRL.button;		// create new button and split device screen between it and the previous slider
	b.value = 1;			// button on device screen toggled on
	b.value = 0;			// button on device screen toggled off

	// define a callback for the next button. The callback will be passed the widget when called 
	// so that you can query and use its new value.
	c = CNTRL.button( (\callback:{ arg widget; widget.value.postln; } ) );

	// pass optional parameters for widget as dictionary. See Control website for details on
	// parameters that can be used.
	d = CNTRL.button( (label:"TEST", page:1, color:"#f00") );
	
	// Define a synth and so we can map controls to volume and freq parameters
	x = SynthDef("help-synth", {| freq = 440, vol = 1 |
		Out.ar(0, SinOsc.ar(freq) * vol);
	}).play;

	// specify parameter and synth
	e = CNTRL.slider( \vol,  x);
	f = CNTRL.slider( \freq, x, (min:440, max:880) );

	
Community
---------

- [Website](http://www.charlie-roberts.com/Control)
- [Forum](http://www.charlie-roberts.com/Control/forum)
	

License
-------
### The MIT License

Copyright (c) <2010> <Charlie Roberts., et. al., >

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

---

