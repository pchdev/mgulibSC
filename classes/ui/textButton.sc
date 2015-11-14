MGU_textButton {

	classvar instanceCount;

	var parent, bounds, string;
	var font, fontsize;
	var view, text;
	var inbounds;
	var bgcolor, bgclickColor, bgoverColor;
	var bdcolor, bdclickColor, bdoverColor;
	var blinkTime;
	var textcolor, textclickColor, textoverColor;

	*new { |parent, bounds, string|
		^this.newCopyArgs(parent, bounds, string).init
	}

	init {

		// checking arguments
		bounds ?? { bounds = Rect(20, 20, 130, 25) };
		font ?? { font = Font("Arial", 10) };
		bgcolor ?? { bgcolor = Color.white };
		bgclickColor ?? { bgclickColor = Color.black };
		bdcolor ?? { bdcolor = Color.black };
		bdclickColor ?? { bdclickColor = Color.white };
		textcolor ?? { textcolor = Color.black };
		textclickColor ?? { textclickColor = Color.white };
		textoverColor ?? { textoverColor = Color.black };
		blinkTime ?? { blinkTime = 0.1 };

		inbounds = Rect(1, 1, bounds.with - 2, bounds.height - 2);

		view = UserView(parent, bounds);
		view.background = bgcolor;
		view.drawFunc = { |thisview|
			Pen.width = 1;
			Pen.strokeColor = bdcolor;
			Pen.strokeRect(inbounds);
		};

		text = StaticText(parent, inbounds);

	}

}