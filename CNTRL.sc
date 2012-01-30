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

	*button {arg args;
		var w;
		if(args == nil, { args = (); });
		args.put(\type, "Button");
				
		^w = this.make(args);		
	}
	
	*knob { arg args;
		var w;
		if(args == nil, { args = (); });
		args.put(\type, "Knob");
		
		^w = this.make(args);
	}
	
	*slider { arg args;
		var w;
		if(args == nil, { args = (); });
		args.put(\type, "Slider");
		
		^w = this.make(args);
	}
	
}

CNTRLWidget : Object {
	var <>master, attributes, <callback, <type, <name, <value, <values, <oscnode;
	
	*new { arg master, attributes;
		attributes.postln;
		^super.newCopyArgs(master, attributes).init;
	}
	
	init {
		var json = "{", oscCallback;
		
		name = attributes[\name];
		type = attributes[\name];
		
		attributes.keys.do({arg key, i;
			key.postln;
			if(key != 'callback', {
				json = json ++ "'" ++ key ++ "':";
			
				if(attributes[key].class.asString == "String", 
					{ json = json ++ "'" ++ attributes[key] ++ "'" },
					{ json = json ++ attributes[key] }
				);
			
				if(i + 1 != attributes.size, { json = json ++ ", "; } );
			});
		});

		json = json ++ "}";
		json.postln;

		master.sendOSC("/control/addWidget", json);
		callback = attributes[\callback];

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