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

//----------------key file-------------
var firebase_key = {
  "type": "service_account",
  "project_id": "talk2me-176916",
  "private_key_id": "7ba994d5720a35f6f63df4f18f2b19672f0a2cbc",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDo4B5w+yOXSYue\nTBxa+DsubHKgfPFJB5VBJ9JeGvvOnt06s495zzNbIEHHHylgfvXV34N20wPT0N+a\nPZfs/BAIYFBpG09l6EZlJqOrdU/56j03LDkxy0L/E2u+qM0SN8f868V+6xx7LyVf\nPSawzzBjjTRgqUigdj8DG3YMsAfstpEdYdKPzteWA9QYob/81Ndn+GSB+xpJoHrB\noSzeOV8oMlgku16tCtetOsFLpIIxZpe+yfrTeE7wpg0JevhqtnpocBsRvT7c6G4U\n5SG8ibROmtZZkdcezjQvsMNbL95W+eVQBk2sYenAqlWe4jkJ9En+4GCK0zAjTg/Q\nq5zVq6PVAgMBAAECggEAMbOVo6jh/3m9n+8gFSVKIj4Yl9qat6qz5vhMDHQnXoGm\n906X9lgr3ozLpScoMlVefANMgFmIhVZcxsNTD8v7UwJVXx1qTHZuwctlnqQ2ycC9\nyG76lP9MAPtA4WwO+o6nsvoSCGS/CyZ0jDdVsOJDclfSgDfZDZA3Ih9kClCunE0x\neF7lYwxo3DGeJUZZx/QArq5e+vgQiCbmWwbf9gYQx9p4gha0lwiLvPv4DgRH0ohv\nxEKB7cNHckuC7vU9kvMQ37I+Bivk64AQq1Bwy8xPQ/5cxPh6KMv0JuNnPA+R5ZyO\ndahDvVVt63cEMho+d92EWJPgA43TLulfFP6yrSj6qQKBgQD6yEposmVxAmsKpk88\nsjBi9xr0zv4c4vVCN2nvuDrb069qlQ07SVnUFYV7pyIUYEpfAB7vPcELPuX2U4jC\nWvQiFQAKy1V3KhQEE0V7fJ2zGUfCzi9vxVGy8JvhErJxSTbrch4KwkiTHNfCnUu2\nKFpQ1gl8EzAVpyPEJ4J0ViAp/QKBgQDtuHP1msbGy3vKKrpguyoycGpkzGekc7vA\nmeWOOH4qo2UJw55Us2jaF2ZoB0pbpV84oHbVkc30n0LZuSHULUvw5PU2xWXhKLq5\nPVQdC8yiHgcfCEL4uGoGZQH3Vg0PLXGjkl6GLIUSHiSjobnJrW0cOtAZwF6BoNsd\nb5zq6c48uQKBgFb6ZKRPS1OYrRtydwTsIFASuCy0JfpK1Rl0Mq/Ly842ax6iN4aa\neCdQl409eFps0x6D4jbDylJmLEf0zPfrp8SyV+NmhmvgEjUfofekTuWxHOx5qoYv\nf5h6TvYPEAiuhfe/CqBcpeMwG70J5Rp2gGyM75ky5nh8EbGUqnBC9sDZAoGBAJMj\n6bqrC8B+sOeo3OYE05CW3nP3xWsYK62CVIFcXFOeAWsyxuzcg9kCROO9J3xkdvUA\n9a0p65Yvgdxr6BGB3BCGV3WIZ5AferwvxsGXzRjHh1bg8TssQowktzvV6o1rbnx1\nm8ZnBhL5z2ntL6cyjme7y5U06gpIe3fT3wfUolAZAoGAJF45vRRuoDWdfdJriKWX\nCekjWjlYQQbTmMf1exY/bMs2URLz8pb8WJE0jKUMwif7d/lllvnA7PpHLvH4D4Uo\nfiRk0n07RF10Tyw8Suasg5YrwZEVU1YrxdBY5C/nJERZWOqapk2G3K5cRQEmLYeH\nQqqFdyfva77SD0XiR2WDaKk=\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-umkdb@talk2me-176916.iam.gserviceaccount.com",
  "client_id": "103469609938816120898",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-umkdb%40talk2me-176916.iam.gserviceaccount.com"}




const util = require('util')

const express = require('express');

//-------global dicts----------------
var users_token_dict = {}
var users_picture_dict = {}
var groups_dict = {}
var existingGroupPIN = {};
//-----------------------------------
var MAX_USER_PER_GROUP = 16

//[INITIALIZE Firebase]
var firebase = require("firebase-admin");
var request = require('request');
const app = express();


//var serviceAccount = require("firebase_private_key.json")
var serviceAccount = firebase_key
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
    //console.log(request);
    messageRecieved(request, function(){
        //console.log('removed request from line');
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

var users_token_dict = {}
var groups_dict = {}
var existingGroupPIN = {};

var test_pin = undefined


function handleMessage(message){
    if (message !== null){
	//console.log("Got message " + message.ttm_message_type)
    console.log("Got message " + message[Object.keys(message)[0]].ttm_message_type)
    message = message[Object.keys(message)[0]]
    console.log("DEBUG: message: " + util.inspect(message,false,null))
    switch(message.ttm_message_type){
        case 'create_group':
        messageHandler.create_group(message);
        break;
    case 'sign_in':
         messageHandler.sign_in(message);
        break;
    case 'token_update':
         messageHandler.token_update(message);
        break;

    case 'join_group':
        messageHandler.join_group(message);
        break; 
    case 'leave_group':
        messageHandler.leave_group(message);
        break; 
    case 'lock_request':
        messageHandler.lock_request(message);
        break;
	case 'device_locked':
		messageHandler.device_locked(message)

        
    default:
        console.log("Unknown message type")
        console.log(message)
     ///
    }
    }
}

var messageHandler = {
'create_group': function handleCreateGroup(message){
    addGroupToDict(message,
        sendGroupCreated)
    
    },
'token_update': function handleTokenUpdate(message){
    	users_token_dict[message.user_id] = message.firebase_token
    },

'sign_in': function handleSignIn(message){
    	users_token_dict[message.user_id] = message.firebase_token
    	users_picture_dict[message.user_id] = message.user_picture_url
    	
    },
    
'lock_request': function handleRequestLock(message){
		sendLockDevice(message.user_id_to_lock)
		//check that the requester is really in the group
    	//if (message.user_id in groups_dict[message.group_pin]){
				//check that the user that should be locked is in the same group
		//		if (message.user_id_to_lock in groups_dict[message.group_pin]){
		//			sendLockDevice(message.user_id)
		//		}
		//}
		
    },


    
'device_locked': function handleDeviceLocked(message){
             
			members = groups_dict[message.group_pin].members
			sendGroupFound(message.user_id, groups_dict[message.group_pin])
            //for each (member in members){
            members.forEach(function(member){
                if (message.user_id != member){
                    sendLockRequestWorked(message.user_id, member)
                }
            })
                
    	    
    },
 
'join_group': function handleJoingGroup(message){
    message.group_pin = message.group_pin.toLowerCase()
    if (groups_dict[message.group_pin] === undefined){
            sendGroupReqFailed(message.user_id, message.group_pin, 'group_does_not_exist')
        }
        else{
        	members = groups_dict[message.group_pin].members
        	if(members.indexOf(message.user_id) == -1){
        			          
    	    	if (members.length >= MAX_USER_PER_GROUP){
    	        sendGroupReqFailed(message.user_id, message.group_pin, 'group_full')
        		}
        		else {
                	members.push(message.user_id)

                	members.forEach(function(member){
                    	if (message.user_id != member){
                        	sendNewGroupMember(message.user_id, member, message.group_pin)
                    		}
                    	})
                	}
    	    }
    	    sendGroupFound(message.user_id, groups_dict[message.group_pin])
	    }

    },
	
	
    
'leave_group': function handleLeaveGroup(message){
	if (groups_dict[message.group_pin] !== undefined){
	members = groups_dict[message.group_pin].members
	members.forEach(function(member){
		if (message.user_id != member){
			sendMemberLeftGroup(message.user_id, member,message.group_pin, message.group_pin)
		}
		})
		index = members.indexOf(message.user_id)
		members.splice(index, 1)
		if (members.length <= 0){
			delete groups_dict[message.group_pin]
			delete existingGroupPIN[message.group_pin]
	}}
	else{
	console.log("Request to leave an imaginary group")
}
    },
}

function addGroupToDict(message, callback){
    var found = false
	var group_pin
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
   
	groups_dict[group_pin] = {group_name:message.group_name, group_pin:group_pin, members:[message.user_id], group_picture_url:message.group_picture_url}
	callback(message.user_id, group_pin)
}

function sendLockRequestWorked(locked_member, message_target){
	var message = { 
        data: {  
		ttm_message_type: 'lock_request_worked',
            //group_pin: group_pin,
            locked_member:leaving_user_id
        }
    }
	sendMessageTo(message, message_target)
}


function sendMemberLeftGroup(leaving_user_id, remaining_user_id, group_pin){
    var message = { 
        data: {  
			ttm_message_type: 'member_left_group',
            group_pin: group_pin,
            user_id:leaving_user_id
        }
    }
	sendMessageTo(message, remaining_user_id)
}


function sendNewGroupMember(new_member_user_id, old_member_user_id, group_pin){
    var message = { 
		data: {  
			ttm_message_type: 'new_group_member',
            user_id:new_member_user_id,
            group_pin: group_pin, 
            group_name: groups_dict[group_pin].group_name,
            group_picture_url: groups_dict[group_pin].group_picture_url,
            user_picture_url: users_picture_dict[new_member_user_id]
            
        }
	}
	sendMessageTo(message, old_member_user_id)
}



function sendLockDevice(user_id){
	var message = {         
        data: {  
			ttm_message_type: 'lock_device',
            user_id: user_id

        }
    }
	sendMessageTo(message, user_id)
}

function sendGroupCreated(user_id, group_pin){
	var message = {         
        data: {  
			ttm_message_type: 'group_created',
            group_pin: group_pin,
            user_id: user_id,
            group_name: groups_dict[group_pin].group_name,
            group_picture_url: groups_dict[group_pin].group_picture_url,
            user_picture_url: users_picture_dict[user_id]
            
        }
    }
	sendMessageTo(message, user_id)
}

function sendGroupReqFailed(user_id, group_pin, reason){
	var message = { 
        data: {  
			ttm_message_type: 'group_req_failed',
            group_pin: group_pin,
            reason: reason,

        }
    };
    
	sendMessageTo(message, user_id)

}


function sendGroupFound(user_id, group){
    pics = []
    //state = []
    group.members.forEach(function(member){
		pics.push(users_picture_dict[member])
		//state.push(users_pictures_
    })	
	var message = { 
        data: {  
			ttm_message_type: 'group_found',
			group_pin: group.group_pin,
            group_name: group.group_name,
            group_members: group.members,
            members_pictures:pics,
            user_id:user_id
           }
    };
    
	sendMessageTo(message, user_id)

}

//------------------------------------------------end of message handler----------------------------
function sendMessageTo(message, user_id){
	message.to = users_token_dict[user_id]
	fcm.send(message, function(err, response){
      if (err) {
            console.log("Something has gone wrong!");
            console.log(message);
            console.log(err);
      } else {
            console.log("sent to: " + user_id);
            console.log("Successfully sent with response: ", message);
      }
	})
}
  


// [END hello_world]
if (module === require.main) {
  // [START server]
  // Start the server
  const server = app.listen(process.env.PORT || 8081, () => {
    const port = server.address().port;
    console.log("Talk2Me server Active");
  });
  // [END server]
}

module.exports = app;
