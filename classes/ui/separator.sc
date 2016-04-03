MGU_hSeparator {

	var <parent, <bounds, <color;
	var <view;

	*new { |parent, bounds, color|
		^this.newCopyArgs(parent, bounds, color).init
	}

	init {

		color ?? { color = Color.black };

		view = UserView(parent, bounds)
		.drawFunc = {
			Pen.line(
				Point(0, 0),
				Point(bounds.width, 0)
			);
			Pen.strokeColor = color;
			Pen.width = 0.5;
			Pen.stroke;
		};
	}

}

MGU_vSeparator {

	var <parent, <bounds, <color;
	var <view;

	*new { |parent, bounds, color|
		^this.newCopyArgs(parent, bounds, color).init
	}

	init {

		color ?? { color = Color.black };

		view = UserView(parent, bounds)
		.drawFunc = {
			Pen.line(
				Point(0, 0),
				Point(0, bounds.height)
			);
			Pen.strokeColor = color;
			Pen.width = 0.5;
			Pen.stroke;
		};
	}

}