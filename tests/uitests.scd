// ParOral interface



w = Window("tester", Rect(100, 100, 800, 700), false, true, s, false);
w.background = Color.white;
w.front;

~bview = UserView(w, Rect(20, 20, 130, 25));
~bview.background = Color.white;
~bview.drawFunc = { |thisview|
	Pen.width = 1;
	Pen.strokeColor = Color.black;
	Pen.strokeRect(Rect(1, 1, 120, 23));
};

~btext = StaticText(~bview, Rect(55, 1, 120, 23));
~btext.string = "template";
~btext.font = Font("Arial", 10);


