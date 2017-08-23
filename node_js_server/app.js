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

//[START basic firebase Messaging handeling]
var ref = firebase.database().ref();

function listenForNotificationRequests() {
  var requests = ref.child('notificationRequests');
  ref.on('value', function(requestSnapshot) {
    var request = requestSnapshot.val();
    console.log(request);
    requestSnapshot.ref.remove();
    //sendNotificationToUser(
    //  request.name, 
    //  request.text,
    //  function() {
    //    requestSnapshot.ref.remove();
    //  }
    //);
  }, function(error) {
    console.error(error);
  });
};

function sendNotificationToUser(username, message, onSuccess) {
  request({
    url: 'https://fcm.googleapis.com/fcm/send',
    method: 'POST',
    headers: {
      'Content-Type' :' application/json',
      'Authorization': 'key='+API_KEY
    },
    body: JSON.stringify({
      notification: {
        title: message
      },
      to : '/topics/user_'+username
    })
  }, function(error, response, body) {
    if (error) { console.error(error); }
    else if (response.statusCode >= 400) { 
      console.error('HTTP Error: '+response.statusCode+' - '+response.statusMessage); 
    }
    else {
      onSuccess();
    }
  });
}

// start listening
listenForNotificationRequests();
//[END basic firebase chat]

// [START hello_world and send notification on web hit]

var FCM = require('fcm-node');
var fcm = new FCM(API_KEY);

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
      } else {
            console.log("Successfully sent with response: ", response);
      }
    });
  
});
// [END hello_world]

if (module === require.main) {
  // [START server]
  // Start the server
  const server = app.listen(process.env.PORT || 8081, () => {
    const port = server.address().port;
    console.log(`App listening on port ${port}`);
    console.log("shit got serious");
  });
  // [END server]
}

module.exports = app;
