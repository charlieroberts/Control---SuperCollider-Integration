CNTRL : Object {
	var <>remoteIP, <>remotePort, <oscout, widgetNumber, widgets;
	
	*new { arg remoteIP = "127.0.0.1", remotePort = 8080;
		^super.newCopyArgs(remoteIP, remotePort).init;
	}
	
	init {
		"CNTRL INIT".postln;
		widgets = Dictionary.new;
		widgetNumber = 0;
		oscout = NetAddr(remoteIP, remotePort);
		oscout.sendMsg("/control/createBlankInterface", "testing", "portrait");
		oscout.sendMsg("/control/pushDestination", "127.0.0.1:57120");
	}
	
	make {arg args;
		var name, widget;
		name = "Widget" ++ widgetNumber;
		
		widgetNumber = widgetNumber + 1;
		
		args.put(\name, name);
		
		widget = CNTRLWidget.new(this, args);
		widgets.put(name, widget);
		
		^widget;
	}
	
	removeWidget{ arg name;
		widgets.removeAt(name);
	}
	
	sendOSC { arg address, value;
		address.postln;
		value.postln;
		oscout.sendMsg(address, value);
	}
	
	slider { arg args;
		var w;
		if(args == nil, { args = (); });
		args.put(\type, "Slider");
		
		w = this.make(args);
		
		^w;
	}
	button { arg args;
		var w;
		if(args == nil, { args = (); });
		args.put(\type, "Button");
		
		w = this.make(args);
		
		^w;
	}
	knob { arg args;
		var w;
		if(args == nil, { args = (); });
		args.put(\type, "Knob");
		
		w = this.make(args);
		
		^w;
	}
}

CNTRLWidget : Object {
	var <>master, attributes, type, name, value, values;
	
	*new { arg master, attributes;
		"MAKING".postln;
		attributes.postln;
		^super.newCopyArgs(master, attributes).init;
	}
	
	init {
		var json = "{";
		
		name = attributes[\name];
		attributes.keys.do({arg key, i;
			json = json ++ "'" ++ key ++ "':";
			
			if(attributes[key].class.asString == "String", 
				{ json = json ++ "'" ++ attributes[key] ++ "'" },
				{ json = json ++ attributes[key] }
			);
			
			if(i + 1 != attributes.size, { json = json ++ ", "; } );
		});

		json = json ++ "}";
		json.postln;

		master.sendOSC("/control/addWidget", json);
	}
	
	remove {
		master.removeWidget(name);
	}
}