MGU_term_Object {

	var <backgroundColor;
	var <defaultFont;
	var <textColor;

	*new {
		^super.new.init;
	}

	init {
		backgroundColor = Color.new255(8, 30, 15, 255);
		textColor = Color.new255(59, 217, 136, 255);
		defaultFont = Font("Courier", 11);
	}

}

	