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






console.log("----------------------check sign in ------------------------------")
sign_in_message = {
    message_type:"sign_in",
    user_id:"builder",
    firebase_token:"builder-TOKEN"
}
handleMessage(sign_in_message)

sign_in_message = {
    message_type:"sign_in",
    user_id:"tester",
    firebase_token:"tester-TOKEN"
}
handleMessage(sign_in_message)

sign_in_message = {
    message_type:"sign_in",
    user_id:"joiner",
    firebase_token:"joiner-TOKEN"
}

handleMessage(sign_in_message)
console.log("users_token_dict (should have 2 users)")
console.log(users_token_dict)

console.log("----------------------check create_group ------------------------------")
console.log("group_dict (should be empty)")
console.log(groups_dict)
create_group_message = {
    message_type:"create_group",
    user_id:"builder",
    group_name:"GROUPYYYYY"
    
}
handleMessage(create_group_message)

console.log("group_dict (need to have one group)")
console.log(groups_dict)
console.log(existingGroupPIN)

create_group_message = {
    message_type:"create_group",
    user_id:"tester",
    group_name:"TESTER-GROUP"
    
}
handleMessage(create_group_message)

console.log("  ===> group_dict (need to have 2 groups)")
console.log(groups_dict)
console.log(existingGroupPIN)





console.log("----------------------check join_group->wrong_pin ------------------------------")
join_group_message_wrong_pin = {
    message_type:"join_group",
    user_id:"joiner",
    group_pin:"aaaa"
    
}

handleMessage(join_group_message_wrong_pin)
console.log("====> group_dict - 1 group, 1 user")
console.log(groups_dict)

console.log("----------------------check join_group->group_found ------------------------------")

join_group_message = {
    message_type:"join_group",
    user_id:"joiner",
    group_pin:test_pin
    
}

handleMessage(join_group_message)
console.log("====> group_dict (need to have one group, 2 users)")
console.log(groups_dict)


console.log("----------------------check leave_group->group still alive ------------------------------")

join_group_message = {
    message_type:"leave_group",
    user_id:"builder",
    group_pin:test_pin
    
}

handleMessage(join_group_message)
console.log("====>group_dict (need to have one group, 1 user - joiner)")
console.log(groups_dict)

join_group_message = {
    message_type:"leave_group",
    user_id:"joiner",
    group_pin:test_pin
    
}

handleMessage(join_group_message)
console.log(" ====>group_dict (need to have zero groups beside tester group)")
console.log(groups_dict)


