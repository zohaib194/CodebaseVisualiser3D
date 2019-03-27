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

        websocket.on('open', function onOpen() {
	        websocket.send(JSON.stringify(payload));
	    })



        websocket.on('message', function incoming(data) {
	        var response = JSON.parse(data)
			messages.push(response)
	    })


	    websocket.on('close', function onClose(code, reason) {
	        var reason = JSON.parse(reason)

	        websocket.close();
	    	resolve({
	    		"messages":messages,
	    		"closer":reason,
	    	})
	    })

    });
}

var testCases =
[
	// Valid cases.
	{
		name: "Valid - new repo - sendAddRequest",
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
		name: "Valid - already existing repo - sendAddRequest",
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
		name: "Invalid - new repo - sendAddRequest",
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

for (var i = 0; i <= testCases.length - 1; i++) {

	let index = i

	test.serial(testCases[i].name, async(t) => {

		var res = await getWebSocketResult(testCases[index].websocketURL, testCases[index].payload)

		t.is(res.messages.length, testCases[index].wantMessage.length)

		for (var j = 0; j <= res.messages.length - 1; j++) {
			t.is(res.messages[j].statustext, testCases[index].wantMessage.messages[j].statustext)
			t.is(res.messages[j].statuscode, testCases[index].wantMessage.messages[j].statuscode)
			t.is(res.messages[j].body.status, testCases[index].wantMessage.messages[j].body.status)
		}

		t.is(res.closer.statuscode, testCases[index].wantCloser.reason.statuscode)
		t.is(res.closer.statustext, testCases[index].wantCloser.reason.statustext)
		t.is(res.closer.body.status, testCases[index].wantCloser.reason.body.status)

	});
}