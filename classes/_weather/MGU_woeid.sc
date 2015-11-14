MGU_woeid {

	var <>app_id, <>cityQuery, pipe, line;

	*new { arg app_id = "wx3cO3zV34HuIEtzZ3gQNApMOy7ovqAN7bFLZv75t9YBQvyKe2OSlBuTc.jd9Q--",
		cityQuery = "Toulouse";
		^super.newCopyArgs(app_id, cityQuery).init
	}

	init {
		var string = "curl \"http://where.yahooapis.com/v1/places.q('" ++ cityQuery ++ "')?appid=\\[" ++ app_id ++ "\\]\"";
		pipe = Pipe.new(string, "r");
		2.do ({ line = pipe.getLine });
		string.postln;
		line.postln;
	}

	getWOEID {
		var woeidInt;
		woeidInt = line.split($>).at(3).split($<).at(0).asInteger;
		^woeidInt;
	}
}



