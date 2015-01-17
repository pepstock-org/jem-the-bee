/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
// required nodejs packages
var https = require('https');
var http = require('http');
var fs = require('fs');
var url = require('url');
var crypto = require('crypto');

// list of variables which are used to
// save the script arguments
var httpUrl = ''
var user = '';
var env = '';
var password = '';
var jcl = '';
var type = '';
var privateKeyFile = '';
var wait = false;
var printOutput = false;
var localPort = 7000;

// internal global variables
// with the default, when applicable
var host = ''
var port = 80;
var jobId = '';

// gets the user
// if POSIX, used the nodejs function
if (process.getuid) {
	user = process.getuid();
} else {
	// otherwise for Windows it uses the environment variable
	user = process.env.USERNAME;
}
// scans all arguments
// starts from 2 because arg[0] is the nodejs executable
// and arg[1] is this script
for (var i = 2; i < process.argv.length; i++) {
	var element = process.argv[i];
	// gets URL of web site of JEM
	if (element == '-host') {
		httpUrl = getArgument(i, 'host');
		i++;
		// gets JEM cluster password
	} else if (element == '-password') {
		password = getArgument(i, 'password');
		i++;
		// gets JCL type
	} else if (element == '-type') {
		type = getArgument(i, 'type');
		i++;
		// gets JCL file path
	} else if (element == '-jcl') {
		jcl = getArgument(i, 'jcl');
		i++;
		// gets teh private key
		// this is necessary ONLY if
		// JEM is working with SocketInterceptor
	} else if (element == '-privateKey') {
		privateKeyFile = fs.readFileSync(getArgument(i, 'privateKey'));
		i++;
	} else if (element == '-privateKeyPwd') {
		// currently you can pass
		// but it's ignored because
		// nodeJs is not able to manage privateKey with
		// password
		i++;
		// gets the initial localport for the HTTP listening
	} else if (element == '-localPort') {
		localPort = getArgument(i, 'localPort');
		i++;
		// gets wait state
	} else if (element == '-wait') {
		wait = true;
		// gets info if the output of the job must be printed
		// to the std output
	} else if (element == '-printOutput') {
		printOutput = true;
	} else {
		// if here, some other argument are passed
		// and this is not allowed
		var err = new Error('JEMW0012 Argument ' + element + ' is unknown');
		throw err;
	}
}
// checks all mandatory arguments
// if URL of web site of JEM is missing, exception
if (!httpUrl) {
	var err = new Error('JEMC0056 Missing required options: host');
	throw err;
}
// if password of JEM cluster is missing, exception
if (!password) {
	var err = new Error('JEMC0056 Missing required options: password');
	throw err;
}
// if JCL file path is missing, exception
if (!jcl) {
	var err = new Error('JEMC0056 Missing required options: jcl');
	throw err;
}
// if the user asks to wait for the end of the job
// it starts a HTTP server in listening mode
// JEM job lifecycle module will contact this HTTP server
// when the job is ended
if (wait) {
	// create a HTTP server
	var HTTPserver = http.createServer(function(request, response) {
		// the request URL MUST be the JOB ID
		if (request.url == '/' + jobId) {
			// sets empty body
			var body = '';
			// creates the call back function
			// to read the body of request
			request.on('data', function(data) {
				body += data;
			});
			// creates the call back function
			// when the body of request is ended
			request.on('end', function() {
				// the first row contains
				// the return cod of the job
				var firstCR = body.indexOf('\n');
				var returnCode = body.substring(0, firstCR);
				// if printOutput is set
				if (printOutput) {
					process.stdout.write('JEMC0246 Content of job ' + jobId + '\n');
					// prints on std output the rest of the body
					// containing the output of the job
					process.stdout.write(body.substring(firstCR + 1, body.length) + '\n');
				}
				// response to JEm that everything is OK
				response.writeHead(200, {
					'Content-Type' : 'text/plain'
				});
				response.end('Nothing\n');
				
				process.stdout.write('JEMC0021 Job '+jobId+' is ended in return-code '+returnCode+ '\n');
				// if the return code is 0, exit in 0
				// otherwise in 1
				if (returnCode == 0) {
					process.exit(0);
				} else {
					process.exit(1);
				}
			});
		} else {
			// if here, JEM is NOT the caller
			// then the access is not authorized
			response.writeHead(401, {
				'Content-Type' : 'text/plain'
			});
			response.end('Not authorized\n');
		}
	});
	// starts the listening on the port
	HTTPserver.listen(localPort, function() {
		process.stdout.write('JEMC0046 HTTP submitter is starting on port ' + localPort + '\n');
		// when here, the port is allocated
		// and it can starts the submit
		// calling to have the environment name
		getClusterName(httpUrl);
	}).on('error', function(err) {
		// if here, the port is already in use
		if (err.code === 'EADDRINUSE') {
			// increments the port and
			// tries with teh new one
			localPort++;
			// sets timeout 250ms
			setTimeout(function() {
				// it tries here with the new port
				// if error, is recursive
				HTTPserver.listen(localPort, function() {
					process.stdout.write('JEMC0046 HTTP submitter is starting on port ' + localPort + '\n');
					// it can starts the submit
					// calling to have the environment name
					getClusterName(httpUrl);
				})
			}, 250);
		}
	});
} else {
	// if here, is not in wait phase
	// it can starts the submit
	// calling to have the environment name
	getClusterName(httpUrl);
}

/**
 * Function to extract the argument
 * i = index of argument
 * what = label which represents the argument (necessary only for message purpose)
 */
function getArgument(i, what) {
	// checks if there is another argument
	// after the name
	if ((i + 1) < process.argv.length) {
		// returns the value
		return process.argv[i + 1];
	} else {
		// if not, arguments are WRONG
		var err = new Error('JEMC0056 Missing required options: '+what);
		throw err;
	}
}

/**
 * Function which calls a JEM node by HTTPS (always HTTPS)
 * and submit the job
 */
function submit() {
	process.stdout.write('JEMW0013 Connecting to JEM node ' + host + ':' + port + '\n');
	// creates the HTTPS option
	// the rejectUnauthorized MUST be false
	// in the headers, connection MUST be keep-alive
	var options = {
		host : host,
		port : port,
		method : 'POST',
		path : '/submit',
		headers : {
			connection : 'keep-alive'
		},
		agent : false,
		rejectUnauthorized : false
	};
	// creates the HTTPS request
	var req = https.request(options, function(res) {
		// always UTF-8
		res.setEncoding('utf8');
		// creates the callback to get the return data
		res.on('data', function(data) {
			// tests status code because nodejs
			// doesn't do
			if (res.statusCode < 200 || res.statusCode > 299){
				// shows the message from JEM
				var err = new Error('HTTP Status Code: '+res.statusCode+', message: '+data);
					throw err;
			}
			// the return data is ALWAYS the JOBID
			jobId = data;
			// closes the socket, sending a SYNC
			res.socket.end('Nothing\n');
			
			process.stdout.write('JEMC0020 Job '+jobId+' is submitted for processing\n');
			// catch the CLOSE of socket to close
			// the process if it has been submitted 
			// with -wait=false
			res.socket.on('close', function(error){
				// if no wait
				if (!wait){
					// exit ALWAYS in 0 without waiting
					process.exit(0);
				}
			});
		});
	}).on('error', function(e) {
		// HTTPS exception
		console.log('JEMW0003E Unable to submit into JEM: ' + e.message);
		throw e;
	});
	// reads the JCL file, in UTF-8
	fs.readFile(jcl, 'utf8', function(err, data) {
		// throw the exception if there is
		if (err)
			throw err;
		// creates a URL query string to insert 
		// in the body of the POST request
		var qString = 'wait=' + wait + '&jcl=' + jcl + '&user=' + user
				+ '&type=' + type + '&env=' + env + '&password=' + password
				+ '&callback=' + localPort + '&printOutput=' + printOutput;
		// checks if the privateKey is set 
		if (privateKeyFile) {
			// reads the private key
			var key = privateKeyFile.toString();
			// creates a crypto object
			var sign = crypto.createSign('RSA-SHA256');
			// sets the user as signature 
			sign.update(user);
			// crypts the user and encode in HEX
			var signature = sign.sign(key, 'hex');
			// adds teh signature value to query string
			qString = qString + '&signature=' + signature;
		}
		// writes on the request the query string (always the first row)
		req.write(qString + '\n');
		// writes the JCl content, to submit
		req.write(data + '\n');
		// closes and submits the HTTPS request
		req.end();
	});
}

/**
 * Calls the JEM web interface to get the name of JEM environment
 * httpUrl = the URL of JEM web interface 
 */
function getClusterName(httpUrl) {
	// parses the URL to get scheme, host and port
	var jemUrl = url.parse(httpUrl, false, true);
	var jemHost = jemUrl.hostname;
	var jemPort = jemUrl.port
	// checks if is HTTPS
	if (jemUrl.protocol == 'https:') {
		// creates the option for HTTPS request
		// the rejectUnauthorized MUST be false
		// in the headers, connection MUST be keep-alive		
		var options = {
			host : jemHost,
			port : jemPort,
			path : '/servlet/getClusterGroupName',
			headers : {
				connection : 'keep-alive'
			},
			agent : false,
			rejectUnauthorized : false
		};
		// submit the get creating the callback
		https.get(options, function(res) {
			// sets UTF-8 encoding
			res.setEncoding('utf8');
			// reads the result of HTTPS get
			res.on('data', function(data) {
				// tests status code because nodejs
				// doesn't do
				if (res.statusCode < 200 || res.statusCode > 299){
					// shows the message from JEM
					var err = new Error('HTTP Status Code: '+res.statusCode+', message: '+data);
						throw err;
				}
				// sets the JEM environment name, returned by JEM
				env = data;
				// calls to have the JEM node
				// to use for submitting
				getMember(jemUrl);
			});
		}).on('error', function(e) {
			// HTTPS exception
			console.log('JEMW0002 Unable to get the name of JEM cluter: ' + e.message);
			throw e;
		});
	} else {
		// if here, uses the HTTP, without SSL
		http.get(url, function(res) {
			// sets UTF-8 encoding
			res.setEncoding('utf8');
			// reads the result of HTTPS get
			res.on('data', function(data) {
				// tests status code because nodejs
				// doesn't do
				if (res.statusCode < 200 || res.statusCode > 299){
					// shows the message from JEM
					var err = new Error('HTTP Status Code: '+res.statusCode+', message: '+data);
						throw err;
				}
				// sets the JEM environment name, returned by JEM
				env = data;
				// calls to have the JEM node
				// to use for submitting
				getMember(jemUrl);
			});
		}).on('error', function(e) {
			// HTTPS exception
			console.log('JEMW0002 Unable to get the name of JEM cluter: ' + e.message);
			throw e;
		});
	}
}

/**
 * Calls the JEM web interface to get the host and port of a JEM node
 * to submit the job
 * jemUrl = the URL of JEM web interface, already parsed
 */
function getMember(jemUrl) {
	// gets host name and port
	var jemHost = jemUrl.hostname;
	var jemPort = jemUrl.port
	// checks if is HTTPS
	if (jemUrl.protocol == 'https:') {
		// creates the option for HTTPS request
		// the rejectUnauthorized MUST be false
		// in the headers, connection MUST be keep-alive
		var options = {
			host : jemHost,
			port : jemPort,
			path : '/servlet/getHttpMember',
			headers : {
				connection : 'keep-alive'
			},
			agent : false,
			rejectUnauthorized : false
		};
		// submit the get creating the callback
		https.get(options, function(res) {
			// sets UTF-8 encoding
			res.setEncoding('utf8');
			// reads the result of HTTPS get
			res.on('data', function(data) {
				// tests status code because nodejs
				// doesn't do
				if (res.statusCode < 200 || res.statusCode > 299){
					// shows the message from JEM
					var err = new Error('HTTP Status Code: '+res.statusCode+', message: '+data);
						throw err;
				}
				// parses the host, semicolon, port
				getHostAndPort(data);
				// carry on with the submit
				submit();
			});
		}).on('error', function(e) {
			// HTTPS exception
			console.log('JEMW0001 Unable to get the members of JEM cluter: ' + e.message);
			throw e;
		});
	} else {
		http.get(url, function(res) {
			// sets UTF-8 encoding
			res.setEncoding('utf8');
			// reads the result of HTTPS get
			res.on('data', function(d) {
				// tests status code because nodejs
				// doesn't do
				if (res.statusCode < 200 || res.statusCode > 299){
					// shows the message from JEM
					var err = new Error('HTTP Status Code: '+res.statusCode+', message: '+data);
						throw err;
				}
				// parses the host, semicolon, port
				getHostAndPort(d);
				// carry on with the submit
				submit();
			});
		}).on('error', function(e) {
			// HTTPS exception
			console.log('JEMW0001 Unable to get the members of JEM cluter: ' + e.message);
			throw e;
		});
	}
}

/**
 * Parses the returned string from JEM web interface of the JEM node to use.
 * The format is = host:port
 * member = is the JEM node to use for submitting
 */
function getHostAndPort(member) {
	// gets the index of semicolon
	var indexSemiColon = member.lastIndexOf(':');
	// checks if there is semicolon
	if (indexSemiColon > -1) {
		// extracts the host and port
		host = member.substring(0, indexSemiColon);
		port = member.substring(indexSemiColon + 1, member.length);
	} else {
		// if not, use the standard port, 80
		host = member;
		port = 80;
	}
}