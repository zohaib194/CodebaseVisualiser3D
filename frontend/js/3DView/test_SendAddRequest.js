import test from 'ava';

const WebSocket = require('ws');

console.log('Test currently being run: ', test.meta.file);

/**
 * Open the web socket and stores messages and closer result.
 *
 * @param      {string}   apiURL   The api url
 * @param      {string}   payload  The payload
 * @return     {Promise}  The web socket result.
 */
function getWebSocketResult (apiURL, payload){
    return new Promise(function (resolve, reject) {
		var messages = new Array()
		try {
			var websocket = new WebSocket(apiURL);
		} catch(err){
			console.log("Error websocket could not initialize: " + err)
			reject(err)
		}

		try {
	        websocket.on('open', function onOpen() {
		        websocket.send(JSON.stringify(payload));
		    })

		} catch(err){
			console.log("Error websocket on open: " + err)
			reject(err)
		}

		try{
	        websocket.on('message', function incoming(data) {
		        var response = JSON.parse(data)
				messages.push(response)
		    })

		} catch(err){
			console.log("Error websocket on message: " + err)
		}
		try {

		    websocket.on('close', function onClose(code, reason) {
		        var reason = JSON.parse(reason)

		        websocket.close();
		    	resolve({
		    		"messages":messages,
		    		"closer":reason,
		    	})
		    })
		} catch(err){
			console.log("Error websocket on close: " + err)
		}
    });
}

var testCases =
[
	// Valid cases.
	{
		websocketURL: "ws://localhost:8080/repo/add",
		payload: {
			uri: "https://github.com/Xillez/ECS.git"
		},
		wantMessage: {
			length: 1,
			messages:
			[
				{
					statuscode: 202,
					statustext: "Accepted",
					body : {
						status: "Cloning"
					}
				}
			]
		},
		wantCloser: {
			reason: {
				statuscode: 201,
				statustext: "Created",
				body: {
					status: "Done"
				}
			}
		}
	},
	{
		websocketURL: "ws://localhost:8080/repo/add",
		payload: {
			uri: "https://github.com/Xillez/ECS.git"
		},
		wantMessage: {
			length: 0,
			messages: []
		},
		wantCloser: {
			reason: {
				statuscode: 409,
				statustext: "Conflict",
				body: {
					status: "Repository already exists"
				}
			}
		}
	},

	// Invalid cases
	{
		websocketURL: "ws://localhost:8080/repo/add",
		payload: {
			uri: "使用非常簡單的語言，因此初學者特別容易使用.git"
		},
		wantMessage: {
			length: 0,
			messages: []
		},
		wantCloser: {
			reason: {
				statuscode: 400,
				statustext: "Bad Request",
				body: {
					status: "Expected URI to git repository"
				}
			}
		}
	},
];



test.serial('Valid - new repo - sendAddRequest', async(t) => {
	try {
		var res = await getWebSocketResult(testCases[0].websocketURL, testCases[0].payload)

		t.is(res.messages.length, testCases[0].wantMessage.length)

		for (var i = 0; i <= res.messages.length - 1; i++) {
			t.is(res.messages[i].statustext, testCases[0].wantMessage.messages[i].statustext)
			t.is(res.messages[i].statuscode, testCases[0].wantMessage.messages[i].statuscode)
			t.is(res.messages[i].body.status, testCases[0].wantMessage.messages[i].body.status)
		}

		t.is(res.closer.statuscode, testCases[0].wantCloser.reason.statuscode)
		t.is(res.closer.statustext, testCases[0].wantCloser.reason.statustext)
		t.is(res.closer.body.status, testCases[0].wantCloser.reason.body.status)

	} catch (err) {
		t.fail("Error: " + err)
	}

});

test.serial('Valid - already existing repo - sendAddRequest', async(t) => {
	try {
		var res = await getWebSocketResult(testCases[1].websocketURL, testCases[1].payload)

		t.is(res.messages.length, testCases[1].wantMessage.length)
		t.is(res.closer.statuscode, testCases[1].wantCloser.reason.statuscode)
		t.is(res.closer.statustext, testCases[1].wantCloser.reason.statustext)
		t.is(res.closer.body.status, testCases[1].wantCloser.reason.body.status)

	} catch (err) {
		t.fail("Error: " + err)
	}

});

test.serial('Invalid - new repo - sendAddRequest', async(t) => {
	try {
		var res = await getWebSocketResult(testCases[2].websocketURL, testCases[2].payload)

		t.is(res.messages.length, testCases[2].wantMessage.length)
		t.is(res.closer.statuscode, testCases[2].wantCloser.reason.statuscode)
		t.is(res.closer.statustext, testCases[2].wantCloser.reason.statustext)
		t.is(res.closer.body.status, testCases[2].wantCloser.reason.body.status)

	} catch (err) {
		t.fail("Error: " + err)
	}
});

