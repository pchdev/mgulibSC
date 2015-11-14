MGU_slider {

	var parent, bounds;
	var color, orientation, initVal;
	var <>type;
	var thisView;
	var <>range, unit;
	var <>step;
	var <>val, graphVal, <>recipient;
	var frameView, <sliderView;
	var text;
	var boundToParameter, boundParameter;

	*new { |parent, bounds, color|
		^this.newCopyArgs(parent, bounds, color).init
	}

	init {

		var mouseActions;

		// checking arguments

		bounds !? { if(bounds.width > bounds.height, { orientation = 0 }, { orientation = 1 }) };
		bounds ?? { if(orientation == 0, {
			bounds = Rect(0, 0, 100, 30) }, { // else vertical
				bounds = Rect(0, 0, 30, 100)}
			)};

		color ?? { color = Color.new255(55,90,101) };

		range ?? { range = [0, 127] };
		initVal ?? { initVal = 0 };
		val = initVal;
		type = Float;

		frameView = UserView(parent, bounds);
		sliderView = UserView(frameView, Rect(1, 1, bounds.width - 1, bounds.height - 1));

		frameView.drawFunc = {
			Pen.width = 0.5;
			Pen.color = Color.black;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height));
		};

		if(orientation == 0, {

			sliderView.drawFunc = { |me|
				Pen.width = 0.5;
				Pen.fillColor = color;
				Pen.strokeColor = Color.black;
				Pen.addRect(Rect(0, 0, (val / range[1]) * me.bounds.width,
					me.bounds.height));
				Pen.fillStroke;
			}}, { // else vertical
				sliderView.drawFunc = {
					Pen.width = 0.5;
					Pen.fillColor = color;
					Pen.fillRect(Rect(0,
						(range[1] - val / range[1]) * sliderView.bounds.height,
						sliderView.bounds.width, (val / range[1]) * sliderView.bounds.height));

		}});

		mouseActions = { |me, x, y, mod, bn, cc|
			var graphval;
			if(orientation == 0, {
				if(x > sliderView.bounds.width, { x = sliderView.bounds.width });
				if(x < 0, { x = 0 });
				if((y >= 0) && (y <= sliderView.bounds.height), {
					if(type == Integer, {
						val = ((x / sliderView.bounds.width) * range[1]).round }, {
						val = ((x / sliderView.bounds.width) * range[1])})
				})},
			{ // else vertical
						if(y > sliderView.bounds.height, { y = sliderView.bounds.height });
						if(y < 0, { y = 0 });
						if((x >= 0) && (x <= sliderView.bounds.width), {
					if(type == Integer, {
						val = (range[1] - ((y / sliderView.bounds.height) * range[1])).round(1) }, {
						val = (range[1] - ((y / sliderView.bounds.height) * range[1]))})
					});
			});

			if(cc == 2, { this.val = initVal });

			sliderView.refresh;
			text.string = val.round(0.01);
			if(val > (range[1] / 2), { text.stringColor = Color.white }, { // else black
				text.stringColor = Color.black });

			if(boundToParameter, { boundParameter.val = val });

		};

		sliderView.mouseDownAction = mouseActions;
		sliderView.mouseMoveAction = mouseActions;

		text = StaticText(frameView, Rect((bounds.width / 2) - 5,
			(bounds.height / 2) - 6, bounds.width / 2, bounds.height / 2));
		text.font = Font("Arial", 10);
		text.string = val;
		text.stringColor = Color.black;
		text.acceptsMouse = false;
		text.align = \topLeft;


	}

	bindToParameter { |parameter|
		boundToParameter = true;
		boundParameter = parameter;
		range = boundParameter.range;
		type = boundParameter.type;

	}

	unbindFromParameter {
		boundToParameter = false;
	}



}