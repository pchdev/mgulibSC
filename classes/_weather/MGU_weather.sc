MGU_weather {

	// Instance variables
	var <>woeid = 623868,
	pipe, array, line,
	<>buildDate, <>city, <>country, <>lat, <>long, <>tempC, <>tempF, <>weatherCode,
	<>weatherDesc, <>windChill, <>windDir, <>windSpeed, <>humidity, <>visibility,
	<>pressure, <>rising, <>sunrise, <>sunset;

	// Class methods
	*new { arg woeid;
		^super.newCopyArgs(woeid).init
	}

	// Instance methods

	init {
		var string = "curl http://weather.yahooapis.com/forecastrss?w=" ++ woeid.asString ++ "&u=c";
		pipe = Pipe.new(string, "r");
		array = Array.new;
		line = pipe.getLine;
		for(1, 31,
			{ array = array.add(line);
				line = pipe.getLine});
		pipe.close;
	}

	refresh {
		this.init;
		this.getBuildDate;
		"Weather : data reloaded".postln;
	}

	getBuildDate {
		var buildDateLine = array.at(9), buildDateString;
		buildDateString = buildDateLine.split($>).at(1).split($<).at(0);
		^buildDateString
	}

	getCity {
		var cityLine = array.at(11), cityString;
		cityString = cityLine.split($").at(1);
		^cityString
	}

	getCountry {
		var countryLine = array.at(11), countryString;
		countryString = countryLine.split($").at(5);
		^countryString
	}

	getLat {
		var latLine = array.at(25), latFloat;
		latFloat = latLine.split($>).at(1).split($<).at(0).asFloat;
		^latFloat
	}

	getLong {
		var longLine = array.at(26), longFloat;
		longFloat = longLine.split($>).at(1).split($<).at(0).asFloat;
		^longFloat
	}

	getTempC {
		var tempFLine = array.at(29), tempFFloat, tempCFloat;
		tempFFloat = tempFLine.split($").at(5).asFloat;
		tempCFloat = (tempFFloat - 32) / 1.8;
		^tempCFloat.round;
	}

	getTempF {
		var tempFLine = array.at(29), tempFFloat;
		tempFFloat = tempFLine.split($").at(5).asFloat;
		^tempFFloat
	}

	getWeatherCode {
		var weatherCodeLine = array.at(29), weatherCodeInt;
		weatherCodeInt = weatherCodeLine.split($").at(3).asInteger;
		^weatherCodeInt
	}

	getWeatherDesc {
		var weatherDescLine = array.at(29), weatherDescString;
		weatherDescString = weatherDescLine.split($").at(1).asString;
		^weatherDescString;
	}

	getWindChill {
		var windChillLine = array.at(13), windChillInt;
		windChillInt = windChillLine.split($").at(1).asInteger;
		^windChillInt
	}

	getWindDir {
		var windDirLine = array.at(13), windDirFloat;
		windDirFloat = windDirLine.split($").at(3).asFloat;
		^windDirFloat
	}

	getWindSpeed {
		var windSpeedLine = array.at(13), windSpeedFloat;
		windSpeedFloat = windSpeedLine.split($").at(5).asFloat;
		^windSpeedFloat
	}

	getHumidity {
		var humidityLine = array.at(14), humidityFloat;
		humidityFloat = humidityLine.split($").at(1).asFloat;
		^humidityFloat
	}

	getVisibility {
		var visibilityLine = array.at(14), visibilityFloat;
		visibilityFloat = visibilityLine.split($").at(3).asFloat;
		^visibilityFloat
	}

	getPressure {
		var pressureLine = array.at(14), pressureFloat;
		pressureFloat = pressureLine.split($").at(5).asFloat;
		^pressureFloat
	}

	getRising {
		var risingLine = array.at(14), risingFloat;
		risingFloat = risingLine.split($").at(7).asInteger.asBoolean;
		^risingFloat
	}

	getSunrise {
		var sunriseLine = array.at(15), sunriseString;
		sunriseString = sunriseLine.split($").at(1).asString;
		^sunriseString
	}

	getSunset {
		var sunsetLine = array.at(15), sunsetString;
		sunsetString = sunsetLine.split($").at(3).asString;
		^sunsetString
	}

}