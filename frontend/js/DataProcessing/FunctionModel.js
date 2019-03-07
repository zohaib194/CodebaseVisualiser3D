/**
 * Container model to contain function metadata.
 *
 * @param      {String}  file           The file
 * @param      {String}  funcStartLine  The function start line
 * @param      {String}  funcEndLine    The function end line
 * @return     {Object}  { description_of_the_return_value }
 */
var FunctionMetaData = (function(file, funcStartLine, funcEndLine) {
	var fileName = file
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

	return {
		getFileName: getFileName,
		getStartLine: getStartLine,
		getEndLine: getEndLine
	}
});