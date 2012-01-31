CNTRL : Object {
	classvar <>remoteIP, <>remotePort, <oscout, widgetNumber, widgets;
	
	*init {arg ip = "127.0.0.1", port = 8080;
		remoteIP = ip;
		remotePort =  port;
		"CNTRL INIT".postln;
		widgets = Dictionary.new;
		widgetNumber = 0;
		
		oscout = NetAddr(remoteIP, remotePort);
		oscout.sendMsg("/control/createBlankInterface", "testing", "portrait");
		oscout.sendMsg("/control/pushDestination", "127.0.0.1:57120");
	}
	
	*make {arg args;
		var name, widget;
		name = "Widget" ++ widgetNumber;
		
		widgetNumber = widgetNumber + 1;
		
		args.put(\name, name);
		
		widget = CNTRLWidget.new(this, args);
		widgets.put(name, widget);
		
		^widget;
	}
	
	*removeWidget{ arg name;
		widgets.removeAt(name);
		oscout.sendMsg("/control/removeWidget", name);
	}
	
	*sendOSC { arg address, value;
		address.postln;
		value.postln;
		oscout.sendMsg(address, value);
	}

	*button {arg ... args;
		var w, args_;
		
		case
		{ args[0] == nil} { args_ = (); }
		{ args[0].class.asString == "Symbol"} {
			if(args[2] == nil,
				{ args_ = (); },
				{ args_ = args[2]; }
			);

			args_.put(\param, args[0]); args_.put(\synth, args[1]); 
		}
		{ args[0].class.asString == "Event"} { args_ = args[0] };
		
		args_.put(\type, "Button");
				
		^w = this.make(args_);		
	}
	
	*knob { arg ... args;
		var w, args_;
				
		case
		{ args[0] == nil} { args_ = (); }
		{ args[0].class.asString == "Symbol"} {
			if(args[2] == nil,
				{ args_ = (); },
				{ args_ = args[2]; }
			);

			args_.put(\param, args[0]); args_.put(\synth, args[1]); 
		}
		{ args[0].class.asString == "Event"} { args_ = args[0] };
		
		args_.put(\type, "Knob");
		
		^w = this.make(args_);
	}
	
	*slider { arg ... args;
		var w, args_;
		case
		
		{ args[0] == nil} { args_ = (); }
		{ args[0].class.asString == "Symbol"} {
			if(args[2] == nil,
				{ args_ = (); },
				{ args_ = args[2]; }
			);

			args_.put(\param, args[0]); args_.put(\synth, args[1]); 
		}
		{ args[0].class.asString == "Event"} { args_ = args[0] };
		
		args_.put(\type, "Slider");
		
		^w = this.make(args_);
	}
	
}

CNTRLWidget : Object {
	var <>master, attributes, <callback, <type, <name, <value, <values, <oscnode, <param, <synth;
	
	*new { arg master, attributes;
		attributes.postln;
		^super.newCopyArgs(master, attributes).init;
	}
	
	init {
		var json = "{", oscCallback, needsComma;
		
		name = attributes[\name];
		type = attributes[\name];
		
		needsComma = 0;
		
		attributes.keys.do({arg key, i;
			
			if((key != 'callback') && (key != \synth) && (key != 'param'), {
				key.postln;
				
				if(needsComma == 1, { json = json ++ ", "; } );
				
				json = json ++ "'" ++ key ++ "':";
			
				if(attributes[key].class.asString == "String", 
					{ json = json ++ "'" ++ attributes[key] ++ "'" },
					{ json = json ++ attributes[key] }
				);
				needsComma = 1;
			});
		});

		json = json ++ "}";
		
		master.sendOSC("/control/addWidget", json);
		callback = attributes[\callback];

		param = attributes[\param];
		synth = attributes[\synth];

		value = -100;
		callback.value(this);
		
		if( (param != nil) && (synth != nil),
			{callback = { arg widget; synth.set(param, widget.value); } }
		);
		
		if(callback == nil, 
			{ 
				oscCallback =  { arg time, responder, msg; value = msg[1]; } 
			},
			{ 
				oscCallback =  { arg time, responder, msg; value = msg[1]; callback.value(this); } 
			}
		);
		
		oscnode = OSCresponderNode(nil, name, oscCallback).add;
	}
	
	remove {
		master.removeWidget(name);
	}
	
	value_ { arg val;
		value = val;
		master.sendOSC("/"++name, val);
	}
}