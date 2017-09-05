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

function messageRecieved(message, callback){
	handleMessage(message)	
	callback()
}


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

MAX_USER_PER_GROUP = 16

var users_token_dict = {}
var groups_dict = {}
var existingGroupPIN = {};

var test_pin = undefined

function handleMessage(message){
    console.log("Got message " + message.message_type)
    switch(message.message_type){
        case 'create_group':
        messageHandler.create_group(message);
        break;
    case 'sign_in':
         messageHandler.sign_in(message);
        break;
    case 'join_group':
        messageHandler.join_group(message);
        break; 
    case 'leave_group':
        messageHandler.leave_group(message);
        break; 
        
    default:
        console.log("Unknown message type")
        console.log(message)
     ///
    }
}

var messageHandler = {
'create_group': function handleCreateGroup(message){
    addGroupToDict(message,
        sendGroupCreated)
    
    },
    
'sign_in': function handleSignIn(message){
    	users_token_dict[message.user_id] = message.firebase_token
    },
    
'join_group': function handleJoingGroup(message){
        if (groups_dict[message.group_pin] === undefined){
            sendGroupReqFailed(message.user_id, message.group_pin, 'group_full')
        }
        else{
            members = groups_dict[message.group_pin].members
    	
        	if (members.length >= MAX_USER_PER_GROUP){
    	        sendGroupReqFailed(message.user_id, message.group_pin, 'group_full')
        	}
        	else {
                members.push(message.user_id)
                sendGroupFound(message.user_id, groups_dict[message.group_pin])
                //for each (member in members){
                members.forEach(function(member){
                    if (message.user_id != member){
                        sendNewGroupMember(message.user_id, member)
                    }
                    })
                }
    	    } 
    },
	
	
    
'leave_group': function handleLeaveGroup(message){

	members = groups_dict[message.group_pin].members
	members.forEach(function(member){
		if (message.user_id != member){
			sendMemberLeftGroup(message.user_id, member)
		}
		})
		index = members.indexOf(message.user_id)
		members.splice(index, 1)
		if (members.length <= 0){
			delete groups_dict[message.group_pin]
			delete existingGroupPIN[message.group_pin]
	}
    },
}

function addGroupToDict(message, callback){
    found = false
    while (!found) {
        var uid = ("0000" + ((Math.random() * Math.pow(36, 4)) | 0).toString(36)).slice(-4);
        if (!existingGroupPIN.hasOwnProperty(uid)) {
            existingGroupPIN[uid] = true;
            group_pin = uid;
          	found = true
        }
    }
    if (test_pin ===undefined){
         test_pin = group_pin
    }
   
	groups_dict[group_pin] = {group_name:message.group_name, group_pin:group_pin, members:[message.user_id]}
	callback(message.user_id, group_pin)
}

function sendMemberLeftGroup(leaving_user_id, remaining_user_id){
    var message = { 
        message_type: 'member_left_group',
        data: {  
            group_pin: group_pin,
            new_member:leaving_user_id
        }
    }
	sendMessageTo(message, remaining_user_id)
}


function sendNewGroupMember(new_member_user_id, old_member_user_id){
    var message = { 
        message_type: 'new_group_member',
        data: {  
            group_pin: group_pin,
            new_member:new_member_user_id
        }
    }
	sendMessageTo(message, old_member_user_id)
}



function sendGroupCreated(user_id, group_pin){
	var message = { 
        message_type: 'group_created',
        data: {  
            group_pin: group_pin,

        }
    }
	sendMessageTo(message, user_id)
}

function sendGroupReqFailed(user_id, group_pin, reason){
	var message = { 
        message_type: 'group_req_failed',
        data: {  
            group_pin: group_pin,
            reason: reason,

        }
    };
    
	sendMessageTo(message, user_id)

}


function sendGroupFound(user_id, group){
	var message = { 
        message_type: 'group_found',
        data: {  
            group_name: group.group_name,
            group_members: group.members,

        }
    };
    
	sendMessageTo(message, user_id)

}


function sendMessageTo(message, user_id){
    message.to = users_token_dict[user_id]
    console.log("-------DEBUG: will be sending message: " + message.message_type + " to:" + user_id +" ---------")
    console.log(message)
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
