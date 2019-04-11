/**
 * Container model to contain function metadata.
 *
 * @param      {String}  file           The file
 * @param      {String}  funcStartLine  The function start line
 * @param      {String}  funcEndLine    The function end line
 * @return     {Object}  object containing get functions.
 */
var FunctionMetaData = (function(file, calls, returnType, declrator_id, funcStartLine, funcEndLine) {
	var fileName = file;
	var calls = calls;
	var returnType = returnType;
	var declratorID = declrator_id;
	var startLine = funcStartLine;
	var endLine = funcEndLine;

	function getFileName(){
		return fileName;
	}

	function getStartLine(){
		return startLine;
	}

	function getEndLine(){
		return endLine;
	}

	function getCalls(){
		return calls;
	}

	return {
		getFileName: getFileName,
		getStartLine: getStartLine,
		getEndLine: getEndLine,
		getCalls: getCalls
	}
});