// Copyright 2017, Google, Inc.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

'use strict';

const express = require('express');


var users_token_dict = {}
var groups_dict = {}
var existingGroupPIN = {};


//[INITIALIZE Firebase]
var firebase = require("firebase-admin");
var request = require('request');
const app = express();

var serviceAccount = require("/home/guytsur7/src/talk2me-176916/firebase_private_key.json")
//Firebase Cloud Messaging Server API key
var API_KEY = "AAAA1R1mny0:APA91bFtlF6Mzm4zE9OaM7hl2xjq0EJRd7n_DalscWeF8RK3dNX7bFWZD3KxYrbsROX-CH-RCLMnhnkIonHtsDRamkobu15fdeh0EjiF4XJtqJ7VJ6WfZtTQxx01FKko2N-PQH-Az9jO"

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://talk2me-176916.firebaseio.com/"
});


//[START basic firebase Messaging handeling and sending]
var ref = firebase.database().ref();
var FCM_NODE = require('fcm-node');
var fcm = new FCM_NODE(API_KEY);



function listenForNotificationRequests() {
  var requests = ref.child('notificationRequests');
  ref.on('value', function(requestSnapshot) {
    var request = requestSnapshot.val();
    console.log(request);
    messageRecieved(request, function(){
        console.log('removed request from line');
        requestSnapshot.ref.remove();
    })
  }, function(error) {
    console.error(error);
  });
};

// start listening
listenForNotificationRequests();
//[END basic firebase chat]

// [START hello_world and send notification on web hit]

var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera) 
	to: 'com.google.android.gms.tasks.zzh@4e44c4d',
        //collapse_key: 'your_collapse_key',
        
        notification: {
            title: 'Title of Thing', 
            body: 'Body of your push Thing' 
        },
        
        //data: {  //you can send only notification or only data(or include both) 
        //    my_key: 'my value',
        //    my_another_key: 'my another value'
        //}
    };


// Say hello!
console.log
app.get('/', (req, res) => {
  res.status(200).send('Hello, world!');
  console.log("web hit, sendnig message");
  fcm.send(message, function(err, response){
      if (err) {
            console.log("Something has gone wrong!");
            console.log(err);
      } else {
            console.log("Successfully sent with response: ", response);
      }
    });
  
});

//[START message handeling]

function messageRecieved(message){
   var type = message.messageType

   switch (type){
	case 'sign_in':
	    handleSignIn(message)
	break;
	case 'create_group':
	    handleCreateGroup(message)
	break;
	}



}

function handleSignIn(message){
	users_token_dict['message.user_id'] = message.firebase_token

}

function handleCreateGroup(message){
	generateUIDWithCollisionChecking(function(group_pin) {
		addGroupToDict(group_pin, message, function(message, group_pin){
				sendGroupCreated(message.user_id, group_pin)
		})			
	})
}
	

//[END message handeling]
function addGroupToDict(group_pin, message){
	groups_dict[group_pin] = {group_name:message.group_name, group_pin:group_pin, members:{}}
}

function generateUIDWithCollisionChecking(group_pin) {
    while (true) {
        var uid = ("0000" + ((Math.random() * Math.pow(36, 4)) | 0).toString(36)).slice(-4);
        if (!existingGroupPIN.hasOwnProperty(uid)) {
            existingGroupPIN[uid] = true;
            group_pin = uid;
        }
    }
}


//[START Sending Messages]
function sendGroupCreated(user_id, group_pin){
	var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera) 
	to: '',

        data: {  //you can send only notification or only data(or include both) 
            group_pin: group_pin,

        }
    };
	
	//sendMessageTo(message, user_id)
	console.log(groups_dict)
}

function sendMessageTo(message, user_id){
	message.to = users_token_dict[user_id]
	fcm.send(message, function(err, response){
      if (err) {
            console.log("Something has gone wrong!");
			console.log(message);
            console.log(err);
      } else {
            console.log("Successfully sent with response: ", response);
      }
	})
}
  


// [END hello_world]
if (module === require.main) {
  // [START server]
  // Start the server
  const server = app.listen(process.env.PORT || 8081, () => {
    const port = server.address().port;
    console.log(`App listening on port ${port}`);
    console.log("shit got serious");
	
	message = {user_id : "a", group_name : "name"}
	handleCreateGroup(message)
	
  });
  // [END server]
}

module.exports = app;
