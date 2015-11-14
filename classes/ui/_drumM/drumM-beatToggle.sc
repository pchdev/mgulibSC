MGU_drumM_beatToggle {

	var parent, bounds;
	var color;
	var thisView;
	var <hitValue, <velValue;
	var drawfunc_on, drawfunc_off;
	var mouseAction;

	*new { |parent, bounds, color|
		^this.newCopyArgs(parent, bounds, color).init
	}

	init {

		bounds ?? { bounds = Rect(0, 0, 20, 40) };
		color ?? { color = Color.new255(55,90,101) };
		thisView = UserView(parent, bounds);

		hitValue = 0;
		velValue = 64;

		drawfunc_on = {
			Pen.width = 0.5;
			Pen.strokeColor = Color.black;
			Pen.fillColor = color;
			Pen.addRect(bounds);
			Pen.fillStroke;
		};

		drawfunc_off = {
			Pen.width = 0.5;
			Pen.strokeColor = Color.black;
			Pen.fillColor = color;
			Pen.fillRect(Rect(bounds.left, bounds.height - 1, bounds.width, 1));
		};

		mouseAction = {|me, x, y, mod|
			if(hitValue == 1, {
				if((y >= (bounds.height / 2)) && (x <= bounds.width), {
					hitValue = 0;
					thisView.drawFunc = drawfunc_off;
					thisView.refresh;
				});
				}, { // else
					if((y < (bounds.height / 2)) && (x <= bounds.width), {
						hitValue = 1;
						thisView.drawFunc = drawfunc_on;
						thisView.refresh;
					})
			})
		};


		thisView.drawFunc = drawfunc_off;
		thisView.mouseDownAction = mouseAction;
		thisView.mouseMoveAction = mouseAction;

	}

	resize { |factor|

	}

}